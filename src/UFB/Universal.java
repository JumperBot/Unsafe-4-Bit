import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
          if(!isDigit(temp.charAt(i2)))confirmed=false;
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
  final static HashMap<Character, Integer> memMap=new HashMap<>(){{
		put(' ', 0); put('A', 1); put('B', 2); put('C', 3); put('D', 4);
    put('E', 5); put('F', 6); put('G', 7); put('H', 8); put('I', 9);
		put('J', 10); put('K', 11); put('L', 12); put('M', 13); put('N', 14);
    put('O', 15); put('P', 16); put('Q', 17); put('R', 18); put('S', 19);
    put('T', 20); put('U', 21); put('V', 22); put('W', 23); put('X', 24);
    put('Y', 25); put('Z', 26);
		put('0', 27); put('1', 28); put('2', 29); put('3', 30); put('4', 31);
    put('5', 32); put('6', 33); put('7', 34); put('8', 35); put('9', 36);
		put('\n', 37);
  }};
  /**
   * Returns a {@code String[]} full of UFB memory pointers parsed from the given {@code String}.
   * <p>Example:</p>
   * <pre><code>
   * convertToMem("ABC12345");
   * {"1", "2", "3", "28", "29", "30", "31", "32"}
   * </code></pre>
   * @param  in          the input {@code String} to be parsed
   * @param  memIndics   tells if the given {@code String} has memory indicators <code>${.*}</code> or <code>$.*</code>.
   * @param  labels      the map used to convert memory indicators into UFB memory pointers
   * @return             the {@code String[]} with all the UFB memory pointers
  */
  public static String[] convertToMem(
    final String in, final boolean memIndics, final HashMap<String, Integer> labels
  ){
    final String ANSI_RESET="\u001B[0m";
    final ArrayList<String> mems=new ArrayList<>();
    boolean backSlash=false;
    boolean memIndicator=false;
    boolean isLabel=false;
    final StringBuilder placeHolder=new StringBuilder();
    if(!memIndics){
      for(final char c:in.toCharArray()){
        if(memMap.containsKey(c))
          mems.add(memMap.get(c).toString());
        else{
          if(c=='\\'){
            if(backSlash){
              backSlash=false;
              mems.add("21");
              mems.add("21");
              for(
                final char c2:
                manPadding(Integer.toString('\\'), 4).toCharArray()
              )
                mems.add(memMap.get(c2).toString());
            }else{
              backSlash=true;
            }
          }else if(backSlash){
            if(c=='n'){
              mems.add("37");
            }else{
              mems.add("21");
              mems.add("21");
              for(
                final char c2:
                manPadding(Integer.toString((int)c), 4).toCharArray()
              )
                mems.add(memMap.get(c2).toString());
            }
            backSlash=false;
          }else{
            mems.add("21");
            mems.add("21");
            for(
              final char c2:
              manPadding(Integer.toString((int)c), 4).toCharArray()
            )
              mems.add(memMap.get(c2).toString());
          }
        }
      }
      return mems.toArray(new String[mems.size()]);
    }
    for(final char c:in.toCharArray()){
      if(c=='$'){
        memIndicator=true;
        placeHolder.append(c);
      }else if(memIndicator){
        placeHolder.append(c);
        if(c=='{')
          isLabel=true;
        else if(isLabel){
          if(c=='}'){
            final String key=placeHolder.substring(2, placeHolder.length()-1);
            if(labels.containsKey(key))
              mems.add(Integer.toString(labels.get(key)));
            else{
              System.out.printf("%s%s%s%s\n",
                ANSI_RESET, "\u001B[91m", formatError(
                  new String[]{convertUnicode(in)},
                  "Memory Index Label Already Replaced By Another",
                  placeHolder.toString(),
                  "Should Be Replaced With The Appropriate Label"
                ), ANSI_RESET
              );
              System.exit(1);
            }
            placeHolder.setLength(0);
            memIndicator=false;
            isLabel=false;
          }
        }else if(!isDigit(c)){
          memIndicator=false;
          for(final String converted:convertToMem(placeHolder.toString(), false, labels))
            mems.add(converted);
          placeHolder.setLength(0);
        }else{
          if(placeHolder.length()==4){
            mems.add(placeHolder.substring(1));
            placeHolder.setLength(0);
            memIndicator=false;
          }
        }
      }else if(memMap.containsKey(c))
        mems.add(memMap.get(c).toString());
      else{
        if(c=='\\'){
          if(backSlash){
            backSlash=false;
            mems.add("21");
            mems.add("21");
            for(
              final char c2:
              manPadding(Integer.toString('\\'), 4).toCharArray()
            )
              mems.add(memMap.get(c2).toString());
          }else{
            backSlash=true;
          }
        }else if(backSlash){
          if(c=='n'){
            mems.add("37");
          }else{
            mems.add("21");
            mems.add("21");
            for(
              final char c2:
              manPadding(Integer.toString((int)c), 4).toCharArray()
            )
              mems.add(memMap.get(c2).toString());
          }
          backSlash=false;
        }else{
          mems.add("21");
          mems.add("21");
          for(
            final char c2:
            manPadding(Integer.toString((int)c), 4).toCharArray()
          )
            mems.add(memMap.get(c2).toString());
        }
      }
    }
    return mems.toArray(new String[mems.size()]);
  }
}