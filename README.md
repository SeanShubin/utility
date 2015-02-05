Utilities
===

While separate utilities can simplify your code base by reducing duplicate code, they complicate your dependency structure.
Be sure the trade off is worth it.
Most importantly, understand the stable dependencies principle.
If the utility is not stable, it belongs in your code base rather than a utility module.
If the utility knows anything about your business logic, it is not stable.
If the utility is not addressing a pain point, it is better to have some duplicate code.
One common guideline for dealing with duplicate code is to not refactor duplication into a reusable component until you have seen it actually used in practice three times.
By waiting until you have seen a pattern three times, you should have enough information to tell the difference between duplication of intent, and coincidental duplication.
For parts of the project that are built at the same time, it is ok to be this aggressive with refactoring duplication.
For parts of the project that are not built at the same time, you should be much more conservative, as you are paying more for the reduction in duplication, in that you are complicating your dependency structure.

File System
===

- Puts integration with the file system behind an interface
- Fully integration tested
- Makes it easy to unit test calling code, as you can implement the interface with a dummy/fake/mock
- No logic, no function chaining; instead, passes directly through to java api calls

Json
===

- Sensible defaults for json marshalling
- Ignore unknown properties to make migration easier
- Convenience function for merging json
- Scala "dynamic" wrapper around json
