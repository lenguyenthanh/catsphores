# Introduction

## Synchronization

In common use, *synchronization* means making two things happen at the same time.
In computer systems, *synchronization* refers to relationships among events - any number of events, and any kind of relationships (before, during, after).

Programmers are often concerned with **synchronization constraints**, which are requirements pertaining to the order of events.

Examples:

- **Serialization**: Event A must happen before Event B.
- **Mutual Exclusion**: Event A and Event B must not happen at the same time.

In computer systems, we often need to satisfy synchronization constraints without the benefit of a clock, either because there is no universal clock, or because we don’t know with fine enough resolution when events occur.

This is what this book is about: software techniques to for enforcing synchronization constraints.

## Execution Models

In order to understand software synchronization, you have to understand how computer programs run.

In the simplest model, computers execute one instruction after another in sequence. In this model, synchronization is trivial; we can tell the order of events by looking at the program. If Statement A comes before Statement B, it will be executed first.

There are two ways things get more complicated:

- **Parallelism**: it has multiple processors running at the same time.
- **Multi threading**: a single processor is running multiple threads at the same time. A thread is a sequence of instructions the execute sequentially. If there are multiple threads, then the processor can work on one for a while and then switch to another, and so on.

For the purpose of synchronization, there is no different between the parallel model and the multithread model. The issue is the same - within one processor (or one thread) we know the order of execution, but between processors (or threads) it is impossible to tell.

## Serialization with messages

For example, you and Bob are friends, and you want to *guarantee* that you will eat lunch before Bob, how can you do that? The simplest example is to instruct Bob not to eat until you call, and make sure you don't call Bob until after lunch. This approach may seem trivial but the underlying ideas, *message passing*, is a real solution for many synchronization problems.

Consider this timeline.

![timeline](images/f1-serialization-with-messages.pgn)

The first column is your thread of execution, the second is Bob's. Within a thread we can denote the order of events
```
a1 < a2 < a3 < a4
b1 < b2 < b3
```
where the relation `a1` < `a2` means that `a1` happened before `a2`.
In general, there is no way to compare events from different threads. For example we don't know who ate breakfast first(is `a1` < `b1`???).
But the message passing (the phone call) we can tell who ate lunch first (`a3` < `b3`). Because of `a4` < `b2` so we get
```
a3 < a4 < b2 < b3
```
which proves that you ate lunch first.

In this case, we would say that you and Bob ate lunch **sequentially**, because we know the order of events, and you ate breakfast **concurrently**, because we don't.

Here the strict definition: **Two events are concurrent if we cannot tell by looking at the program which will happen first.**

Sometimes we can tell, after the program runs, which happened first, but often not, and even if we can, there is no guarantee that we will get the same result the next time.
