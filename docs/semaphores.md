# Semaphores

A semaphore has a non-negative number of permits available.

- Acquiring a permit decrements the current number of permits.
- Releasing a permit increases the current number of permits.
- An acquire that occurs when there are no permits available results in semantic blocking until a permit becomes available.

## Why semaphores

- Semaphores impose deliberate constraints that help programmers avoid errors.
- Solutions using semaphores are often clean and organized, making it easy to demonstrate their correctness.
- Semaphores can be implemented on many systems, so solutions that use semaphores are portable and usually efficient.
