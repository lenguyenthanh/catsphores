# Semaphores

A semaphore has a non-negative number of permits available.

- Acquiring a permit decrements the current number of permits.
- Releasing a permit increases the current number of permits.
- An acquire that occurs when there are no permits available results in semantic blocking until a permit becomes available.

## Semaphore's characteristic

- In general, there is no way to know before a thread decrements a semaphore whether it will block or not (in specific cases you might be able to prove that it will or will not)

- After a thread increments a semaphore and another thread gets woken up, both threads continue running concurrently. There is no way to know which thread, if either, will continue immediately.

- When you signal a semaphore, you don't necessarily know whether another thread is waiting, so the number of unblocked threads maybe zero.

## Why semaphores

- Semaphores impose deliberate constraints that help programmers avoid errors.
- Solutions using semaphores are often clean and organized, making it easy to demonstrate their correctness.
- Semaphores can be implemented on many systems, so solutions that use semaphores are portable and usually efficient.

## Semaphores in Cats Effect

[Cats Effect](https://typelevel.org/cats-effect/) has native support for [Semaphores](https://typelevel.org/cats-effect/docs/std/semaphore)
