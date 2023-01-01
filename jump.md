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

<h1 class="center_text"><a href="jump.html">JUMP</a></h1>

<br>

<h2>All languages have conditional statements in one form or another.</h2>

<blockquote>
This language just makes you a bit more conscious before and after implementing them.
</blockquote>

<h3>A jump statement can work like an if statement or an over-calculated while loop.</h3>
<h4>Jump statements conditionally jump to a zero-indexed command number, not a line number.</h4>
<blockquote>
<p>You can take note of the command number by adding a comment to the end of the line.</p>
<p>wvar 38 "Hi" // 0</p>
<p>print 38 // 1</p>
</blockquote>

<pre>
<span class="comment">// Jump to command number 0 only if "$038" > <span> </span>"$250"</span>
<span class="command">jm</span> <span class="index">38 250</span>

<span class="comment">// Jump to command number 0 only if "$038" < <span> </span>"$250"</span>
<span class="command">jl</span> <span class="index">38 250</span>

<span class="comment">// Jump to command number 0 only if "$038" == "$250"</span>
<span class="command">je</span> <span class="index">38 250</span>

<span class="comment">// Jump to command number 0 only if "$038" != "$250"</span>
<span class="command">jne</span> <span class="index">38 250</span>
</pre>

<blockquote>
Non-numerical data is first transformed into a hash before the conditional check.
</blockquote>

<br>

<a href="math.html" class="arrows" style="float: left;">⏪️</a>
<a href="nothingness.html" class="arrows" style="float: right;">⏩</a>
