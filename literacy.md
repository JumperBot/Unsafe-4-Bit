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
</style>

<br>

<h1 class="center_text"><a href="literacy.html">LITERACY</a></h1>

<br>

<h2>A UFB program can be either illiterate or literate.</h2>

<blockquote>
<h6>b: able to read and write</h6>
<h5>- <a href="https://www.merriam-webster.com/dictionary/literate">Merriam-Webster Dictionary</a></h5>
</blockquote>

<div style="display: flex;">
<div style="width: 50%">
<h3>This program is literate:</h3>
<pre>
<span class="command">wvar </span> <span class="index">38</span> <span class="string">"Hello"</span>
<span class="command">read </span> <span class="index">50</span>
<span class="command">print</span> <span class="string">"<span class="index">$038</span>, <span class="index">$050</span>!\n"</span>
</pre>
</div>
<div style="width: 50%">
<h3>This program is illiterate:</h3>
<pre>
<span class="command">print</span> <span class="string">"Hello, World!\n"</span>


</pre>
</div>
</div>

<h3>Literacy is the boundary between knowing more about the outside world and oneself...</h3>
<h3>But at the cost of more work and performance inefficiency.</h3>

<br>

<a href="hello_world.html" class="arrows" style="float: left;">⏪️</a>
<a href="variables.html" class="arrows" style="float: right;">⏩</a>
