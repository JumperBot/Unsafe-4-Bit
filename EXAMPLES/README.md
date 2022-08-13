# Commands

## Default-16

|Command|Expanded Meanings|Argument Counts|Argument Types						 |
|:-----:|:---------------:|:-------------:|:------------------------:|
|wvar		|write to memory	|\*							|memory indexes						 |
|nvar		|null memory			|1							|memory index					 		 |
|trim		|trim memory			|2							|memory indexes					 	 |
|add		|add							|2							|memory indexes						 |
|sub		|subtract					|2							|memory indexes						 |
|mul		|multiply					|2							|memory indexes						 |
|div		|divide						|2							|memory indexes						 |
|mod		|modulus					|2							|memory indexes						 |
|rmod		|reverse modulus\*|2							|memory indexes						 |
|nop		|no operation			|0							|													 |
|jm			|jump if more than|3							|memory indexes, command no|
|jl			|jump if less than|3							|memory indexes, command no|
|je			|jump if equal		|3							|memory indexes, command no|
|jne		|jump if not equal|3							|memory indexes, command no|
|print	|print						|\*							|memory indexes, command no|
|read		|read							|1							|memory index							 |

---

### Disclosure

---

`rmod` returns the left side of the `div` operator.

`mod` returns the right side of the `div` operator.

`command no` doesn't count by line, it counts by commands written.

`-|, \t` can divide between a commans and its arguments.

`a-zA-Z0-9 \n-|,\t` are the only characters allowed upon compilation.

---

# Memory

|Index	|Values		|
|:-----:|:-------:|
|0			|' '			|
|1-26		|'A'-'Z'	|
|27-37	|'0'-'9'	|
|38-255	|'\u0000'	|

---

# Comments

|Comment|Expanded Meaning(s)|Is Multi-line|
|:-----:|:-----------------:|:-----------:|
|//			|Normal Comment			|No						|
|/\*\*/	|Multi-line Comment	|Yes					|

---

# Example

```python
// This program creates an endless background of "Hello World"s.


/*
Write "Hello World" to memory index: 38
*/
wvar  38  8  5  12  12  15  0  23  15  18  12  4
// Print the variable residing in memory index: 38
print 38
// Empty out memory index 38 to avoid memory leakage
nvar  38
/* Jump to command no 0
   if the values in memory indexes 0 and 0 are equal */
je    0  0  0
```
