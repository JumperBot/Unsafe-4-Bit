# Commands

## Default-16

|Command           |Expanded Meaning					|Argument Count |Argument Type(s)						|
|:----------------:|:------------------------:|:-------------:|:-------------------------:|
|nop							 |no operation							|0							|														|
|nvar	\| read	     |null memory	\| read input |1							|memory index								|
|trim              |trim memory index					|2							|memory indexes, number			|
|add  \| sub       |add \| subtract						|2							|memory indexes							|
|mul  \| div       |multiply \| divide				|2							|memory indexes							|
|mod  \| rmod		   |modulus \| reverse mod		|2							|memory indexes							|
|jm   \| jl		 		 |jump if more/less than		|3							|memory indexes, command no	|
|je	  \| jne		   |jump if equal/not equal		|3							|memory indexes, command no	|
|wvar \| print     |write to memory	\| print	|\*							|memory indexes							|

---

### More Info

---

- `rmod` returns the left side of the `div` operator.

- `mod` returns the right side of the `div` operator.

- `command no` doesn't know what a *"line"* is.

- `-|, \t` can divide between a command and its argument(s).

- `a-zA-Z0-9 \n-|,\t` are the only characters allowed upon compilation.

- All non-allowed characters are neglected by the compiler.

- Only the compiler will stop you from writing to the `ROM`.

- Manually compiling to a `.ufbb` file is NOT recommended.

---

# Memory

|Index	|Values		|ROM?	|
|:-----:|:-------:|:---:|
|0			|' '			|Yes	|
|1-26		|'A'-'Z'	|Yes	|
|27-36	|'0'-'9'	|Yes	|
|37			|'\n'			|Yes	|
|38-255	|'\u0000'	|No		|

---

# Comments

|Comment|Expanded Meaning		|Multi-line?	|
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
/* Jump to command no, 0
   if the values in memory indexes 0 and 0 are equal */
je    0  0  0
```
