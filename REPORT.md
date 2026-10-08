# Assignment 2

## Setup

The project uses `int` values in a dynamic array, a linked list, and a min-heap. The benchmark uses `Random(42)`, four sizes from 100 to 100,000, one warm-up, and five measured runs. It saves median time and operation counts to `results/results.csv`. W1–W3 setup is outside the timed part; W4 includes heap inserts and removals. This run used OpenJDK 26 on Linux.

## Complexity

| Structure / operation | Best case | Average case | Worst case | Extra space | Reason |
|---|---:|---:|---:|---:|---|
| DynamicArray `add(x)` | Ω(1) | Θ(1), amortized | O(n) | O(n) on resize | A full array is copied; doubling makes most appends cheap. |
| DynamicArray `add(i,x)` | Ω(1) | Θ(n) | O(n) | O(1), plus resize | End insert needs no shift; other inserts shift values. |
| DynamicArray `remove(i)` | Ω(1) | Θ(n) | O(n) | O(1) | Removing the last value needs no shift. |
| DynamicArray `get(i)` | Ω(1) | Θ(1) | O(1) | O(1) | The index reads one array cell. |
| DynamicArray `contains(x)` | Ω(1) | Θ(n) | O(n) | O(1) | The search may check every value. |
| MyLinkedList `add(x)` | Ω(1) | Θ(1) | O(1) | O(1) new node | The tail gives direct access to the end. |
| MyLinkedList `add(i,x)` | Ω(1) | Θ(n) | O(n) | O(1) new node | Other positions need a walk through the list. |
| MyLinkedList `remove(i)` | Ω(1) | Θ(n) | O(n) | O(1) | The list must find the previous node. |
| MyLinkedList `get(i)` | Ω(1) | Θ(n) | O(n) | O(1) | The list follows links from the head. |
| MyLinkedList `contains(x)` | Ω(1) | Θ(n) | O(n) | O(1) | The search may check every node. |
| MinHeap `insert(x)` | Ω(1) | Θ(log n) | O(n) | O(n) on resize | The value moves up; a full array must be copied. |
| MinHeap `peekMin()` | Ω(1) | Θ(1) | O(1) | O(1) | The minimum is at the root. |
| MinHeap `extractMin()` | Ω(1) | Θ(log n) | O(log n) | O(1) | The last value may move down one tree path. |

## Loop invariants

### `DynamicArray.contains(x)`

**Invariant:** All elements before `i` differ from `x`. **Initialization:** True at `i = 0`. **Maintenance:** If `values[i] != x`, incrementing `i` preserves the invariant. **Termination:** At `i == size`, every element was checked. **Correctness:** The method returns `true` when it finds `x`; otherwise, it correctly returns `false`.

### `MinHeap.siftDown(parent)`

**Invariant:** Heap order holds except possibly between `parent` and its children. **Initialization:** After the root is replaced in `extractMin`, only the root may violate heap order. **Maintenance:** Swapping with the smaller child moves the possible violation down one level. **Termination:** The parent has no children or is no greater than its smaller child. **Correctness:** Heap order is restored throughout the tree.


| Workload | Time | Operation counts |
|---|---|---|
| W1 — Random access | ![W1 time](results/plots/w1_time.png) | ![W1 counts](results/plots/w1_operations.png) |
| W2 — Search | ![W2 time](results/plots/w2_time.png) | ![W2 counts](results/plots/w2_operations.png) |
| W3 — Insert and remove | ![W3 time](results/plots/w3_time.png) | ![W3 counts](results/plots/w3_operations.png) |
| W4 — Priority processing | ![W4 time](results/plots/w4_time.png) | ![W4 counts](results/plots/w4_operations.png) |
