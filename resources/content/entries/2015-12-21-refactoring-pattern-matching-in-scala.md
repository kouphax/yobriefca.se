---

> ```scala
> collect[B](pf: PartialFunction[A, B]): Option[B]
> ```
> ```
> Returns a scala.Some containing the result of applying pf 
> to this scala.Option's contained value, if this option is 
> nonempty and pf is defined for that value.
> ```

This gives us the power of pattern matching so we can transform the input `Option` in to what we want __if and only if__ the input satisfies our conditions, falling back to `None` if it doesn't.  You can think of `collect` like a conditional `map`