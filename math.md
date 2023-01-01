---
layout: default
---

<style>
	a {
    text-decoration: none;
		font-weight: bold;
	}
	.center_text {
		text-align: center;
	}
	.arrows {
		font-size: 50px;
	}
	.command {
		color: red;
	}
	.string {
		color: yellow;
	}
	.index {
		color: orange;
	}
	.comment {
		color: grey;
	}
</style>

<br>

<h1 class="center_text"><a href="math.html">Math</a></h1>

<br>

<h2>Hold your horses!</h2>
<h2>We're not manually solving for the total area of one dispersed floating field of magnets.</h2>

<h3>We can always start with the basics... and dive into whatever is next.</h3>

<pre>
<span class="comment">// It's a recurring theme...</span>
<span class="comment">// all commands that can, will always place the output to the first given memory index.</span>

<span class="comment">// Add the data in memory index 38 and 250.</span>
<span class="command">add</span> <span class="index">38 250</span>

<span class="comment">// Subtract the data in memory index 250 to the data in memory index 38.</span>
<span class="command">sub</span> <span class="index">38 250</span>

<span class="comment">// Multiply the data in memory index 250 to the data in memory index 38.</span>
<span class="command">mul</span> <span class="index">38 250</span>

<span class="comment">// Divide the data in memory index 250 to the data in memory index 38.</span>
<span class="command">sub</span> <span class="index">38 250</span>
</pre>

<pre>
<span class="command">ROM</span>: <span class="index"> 0 - 37 </span> = <span class="string">' ', 'A'-'Z', '0'-'9', '\n'</span>
<span class="command">RWM</span>: <span class="index">38 - 255</span> = <span class="string">'\u0000'</span>
</pre>

<h3>You can access these variables (no matter their length) by specifying their memory index.</h3>
<h3>It can, for some reason, be done in many different ways... as expected.</h3>

<pre>
<span class="comment">// The default way of doing it.</span>
<span class="index">38</span>

<span class="comment">// Inside a string.</span>
<span class="string">"<span class="index">$038</span>"</span>

<span class="comment">// A string literal.</span>
<span class="string">"Hello!"</span>

<span class="comment">// Using a label.</span>
<span class="command">label </span><span class="index">38 </span>var
<span class="index">${var}</span>
</pre>

<blockquote>
There are many more ways of doing this, but those are the basics!
</blockquote>

<br>

<h3>Here are some basic commands for manipulating the memory:</h3>

<pre>
<span class="comment">/&ast; Write a variable at memory index 38 that contains the data:</span>
<span class="comment"> "Hello! How are you?" &ast;/</span>
<span class="command">wvar </span><span class="index">38 </span><span class="string">"Hello! How are you?"</span>

<span class="comment">// Trim memory index 38 so that it would only have a length of 6 characters.</span>
<span class="command">trim </span><span class="index">38 </span> 6

<span class="comment">// Annihilate memory index 38; free a memory index for future use.</span>
<span class="command">nvar </span><span class="index">38</span>

<span class="comment">// This accepts user input and stores the data to memory index 38.</span>
<span class="command">read</span> <span class="index">38</span>

<span class="comment">// Print the data in memory index 38 to the terminal.</span>
<span class="command">print</span> <span class="string">"<span class="index">$038</span>\n"</span>
</pre>

<br>

<a href="literacy.html" class="arrows" style="float: left;">⏪️</a>
<!--<a href="variables.html" class="arrows" style="float: right;">⏩</a>-->