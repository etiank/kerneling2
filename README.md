# Kernel Image Processing

A Java-based image processing application that applies convolution kernels to images using three different execution modes: sequential, multithreaded, and distributed (MPI).

Built as a university project for exploring parallel and distributed computing concepts.

---

## What it does

The app lets you load an image, pick a convolution filter (kernel), and process it. The same result can be produced three ways: sequentially on a single thread, in parallel across CPU threads, or distributed across multiple nodes using MPI - making it easy to compare performance.

### Supported filters

| Filter | Effect |
|---|---|
| Identity | No change (passthrough) |
| Sharpen | Enhances edges and fine detail |
| Box Blur | Simple uniform blur |
| Gaussian Blur | Smooth, natural-looking blur |
| Edge Detection | Highlights edges (Laplacian) |
| Emboss | Gives a 3D raised effect |
| Sobel | Detects horizontal edges |
| Custom | Enter your own 3×3 kernel values |

---

## Modes

### Sequential
Single-threaded convolution. Straightforward - processes pixels one by one.

### Parallel
Multithreaded convolution. Splits the workload across available CPU cores using Java threads.

### Distributed (MPI)
Uses [MPJ Express](http://mpj-express.org/) to distribute image strips across multiple processes/nodes. The root process handles the GUI and coordinates work:
1. Broadcasts the kernel and image dimensions to all worker processes
2. Scatters image strips (`Scatterv`) to each worker
3. Each worker convolves its strip independently
4. Results are gathered back (`Gatherv`) and assembled into the final image

---

## Requirements

- Java 8+
- [MPJ Express](http://mpj-express.org/) (for distributed mode)
