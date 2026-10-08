#!/usr/bin/env python3
"""Create benchmark time and operation-count charts from results.csv."""

import csv
from collections import defaultdict
from pathlib import Path

import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.ticker import LogFormatterMathtext


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "results" / "results.csv"
OUTPUT = ROOT / "results" / "plots"


def load_rows():
    with SOURCE.open(newline="", encoding="utf-8") as stream:
        return list(csv.DictReader(stream))


def series_key(row):
    if row["workload"] == "W3":
        return f'{row["structure"]} ({row["variant"]})'
    return row["structure"]


def draw():
    rows = load_rows()
    OUTPUT.mkdir(parents=True, exist_ok=True)
    for workload in ("W1", "W2", "W3", "W4"):
        selected = [row for row in rows if row["workload"] == workload]
        if not selected:
            continue
        grouped = defaultdict(list)
        for row in selected:
            grouped[series_key(row)].append(row)
        for values in grouped.values():
            values.sort(key=lambda row: int(row["n"]))

        fig, ax = plt.subplots(figsize=(8, 5))
        for label, values in grouped.items():
            ax.plot([int(row["n"]) for row in values],
                    [float(row["time_ms"]) for row in values],
                    marker="o", label=label)
        ax.set_title(f"{workload}: median runtime")
        ax.set_xlabel("n (elements)")
        ax.set_ylabel("Time (ms)")
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.grid(True, which="both", alpha=0.25)
        ax.legend()
        fig.tight_layout()
        fig.savefig(OUTPUT / f"{workload.lower()}_time.png", dpi=160)
        plt.close(fig)

        fig, axes = plt.subplots(3, 1, figsize=(9, 10), sharex=True)
        for ax, metric in zip(axes, ("steps", "moves", "comparisons")):
            metric_values = []
            for label, values in grouped.items():
                metric_values.extend(int(row[metric]) for row in values)
                ax.plot([int(row["n"]) for row in values],
                        [int(row[metric]) for row in values],
                        marker="o", label=label)
            ax.set_ylabel(metric.capitalize())
            if any(value > 0 for value in metric_values):
                # Counts span several orders of magnitude; symlog keeps zero
                # observations visible while labeling positive values as 10^n.
                ax.set_yscale("symlog", linthresh=1)
            else:
                # An all-zero metric is clearer with a single zero baseline.
                ax.set_ylim(-1, 1)
                ax.set_yticks([0])
            ax.set_xscale("log")
            sizes = sorted({int(row["n"]) for values in grouped.values() for row in values})
            ax.set_xticks(sizes)
            ax.xaxis.set_major_formatter(LogFormatterMathtext(base=10))
            ax.grid(True, which="both", alpha=0.25)
        axes[0].set_title(f"{workload}: physical operation counts")
        axes[-1].set_xlabel("n (elements)")
        axes[0].legend(ncol=2, fontsize="small")
        fig.tight_layout()
        fig.savefig(OUTPUT / f"{workload.lower()}_operations.png", dpi=160)
        plt.close(fig)


if __name__ == "__main__":
    draw()
