import java.util.Arrays;

class Universal{
  private Universal(){}
  /**
   * Returns a {@code String} with added zeroes to its left side.
   * <p>Example:</p>
   * <pre><code>
   * manPadding("1", 4);
   * return "0001"
   * </code></pre>
   * @param  str  the input {@code String} to add left-side padding to
   * @param  i    the appropriate length of {@code str} after padding
   * @return      the {@code str} with left-side padding
   * @see         String#format(String, Object...)
  */
	public static String manPadding(final String str, final int i){
    return String.format(
      new StringBuilder("%").append(i).append("s").toString(),
      str
    ).replace(" ", "0");
  }
  /**
   * Returns a {@code boolean} that signifies if a given {@code char} is a digit.
   * <p>Example:</p>
   * <pre><code>
   * isDigit('0');
   * true
   * </code></pre>
   * @param  c    the input {@code char} to check
   * @param  i    the appropriate length of {@code str} after padding
   * @return      true if {@code c} is a digit, false otherwise
   * @see         Character#isDigit(char)
  */
  public static boolean isDigit(final char c){
    // BeCoz Character.isDigit has too much function overhead.
    return (c>47&&c<58);
  }
  /**
   * Returns an {@code int} parsed from the input {@code String}.
   * <p>Example:</p>
   * <pre><code>
   * toIntAbsolute("100");
   * 100
   * </code></pre>
   * @param  s    the input {@code String} to parse
   * @return      the {@code int} representation of {@code s}
   * @see         Integer#parseInt(String)
  */
  public static int toIntAbsolute(final String s){
		// BeCoz Integer#parseInt() is slow and try-catch is expensive.
    int result=0;
    for(final char c:s.toCharArray()){
      result+=c-48;
      result*=10;
    }
    return result/10;
  }
  /**
   * Returns a {@code String} representation of the input {@code String[]}.
   * <p>Each element in the array is separated by one whitespace character.</p>
   * <p>Example:</p>
   * <pre><code>
   * lineGen(new String[]{"haha", "ha"});
   * "haha ha"
   * </code></pre>
   * @param  line the input {@code String[]} to re-represent
   * @return      the {@code String} representation of {@code line}
   * @see         Arrays#toString(Object[])
  */
	public static String lineGen(final String[]line){
    final String out=Arrays.toString(line).replace(", ", " ");
		return out.substring(1, out.length()-1).concat("\n\n");
	}
  /**
   * Returns a formatted {@code String} error from the input.
   * <p>Example:</p>
   * <pre><code>
   * final String[] line={"unknownCommand", "0", "90"};
   * </code></pre>
   * <pre><code>
   * formatError(line, "Command", line[0], "Does Not Exist");
   * """Error: |
   *        Command: |
   *            \"unknownCommand\" Does Not Exist: |
   *                unknownCommand 0 90
   * """
   * </code></pre>
   * @param  line the input {@code String[]} that represents the whole line of code
   * @param  in   the input {@code String...} that elaborates the error even further
   * @return      the {@code String} representation of the error itself
  */
  public static String formatError(final String[] line, final String... in){
    if(in.length<4)
      return new StringBuilder("Error: |\n")
            .append(String.format("    %s: |\n", in[0]))
            .append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
            .append(String.format("            %s", lineGen(line))).toString();
    else
      return new StringBuilder("Error: |\n")
            .append(String.format("    %s: |\n", in[0]))
            .append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
            .append(String.format("            %s", convertUnicode(lineGen(line).replace("\n\n", "\n"))))
            .append(String.format("        %s: |\n", "Which Is When Converted"))
            .append(String.format("            %s", in[3])).toString();
  }
  /**
   * Returns a formatted {@code String} that converts all unicode points.
   * <p>Example:</p>
   * <pre><code>
   * convertUnicode("uu0032");
   * " "
   * </code></pre>
   * @param  in   the input {@code String} to be scouted of unicode points
   * @return      the {@code String} with all the unicode points converted
  */
  public static String convertUnicode(final String in){
    if(in.length()<6)return in;
    String temp=in;
    // Regex slow ._.
    for(int i=0;i<temp.length()-6;i++){
      if(temp.substring(i, i+2).toLowerCase().equals("uu")){
        boolean confirmed=true;
        for(int i2=i+2;i2<6;i2++)
          if(!Universal.isDigit(temp.charAt(i2)))confirmed=false;
        if(confirmed)
          temp=new StringBuilder(temp.substring(0, i))
            .append((char)toIntAbsolute(
              temp.substring(i+2, i+6)
            ))
            .append(temp.substring(i+6)).toString();
      }
    }
    return temp;
  }
}