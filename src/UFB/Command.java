/**
 *
 *	Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.
 *	Copyright (C) 2022  JumperBot_
 *
 *	This program is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
**/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import java.util.concurrent.ExecutorService;

import java.util.regex.Pattern;

class Command{
  final ArrayList<Integer> compiled=new ArrayList<>();
  final StringBuilder errors=new StringBuilder();
  final ExecutorService executor;
  final HashMap<String, Integer> binaryMap;
  final Pattern jumps;
  final Pattern maths;
  final Pattern pwvar;
  final String[] line;
  final String[] realLine;
  boolean cancelOptimization=false;
  public boolean cancelOptimization(){
    return cancelOptimization;
  }
  public int[] getCompiled()throws Exception{
    final int[] returnMe=new int[compiled.size()];
    for(int i=0;i<returnMe.length;i++)
      returnMe[i]=compiled.get(i).intValue();
    return returnMe;
  }
  public String getErrors(){
    return errors.toString();
  }

  private Command(final String[] line, final String[] realLine, final ExecutorService executor,
  final Pattern jumps, final Pattern maths, final Pattern pwvar,
  final HashMap<String, Integer> binaryMap){
    this.line=line;
    this.realLine=realLine;
    this.executor=executor;
    this.binaryMap=binaryMap;
    this.jumps=jumps;
    this.maths=maths;
    this.pwvar=pwvar;
    this.line[0]=this.line[0].toLowerCase();
  }
  private void compile(){
    executor.execute(new Runnable(){
      public void run(){
        final GenericCommand com=getCommand(binaryMap.get(line[0]));
        errors.append(com.getErrors());
        for(final int i:com.getCompiled())
          compiled.add(i);
      }
    });
  }
  private GenericCommand getCommand(final int comInd){
    switch(comInd){
      case 0:
        return new WvarCommand(line, realLine);
      case 2:
        return new TrimCommand(line, realLine);
      case 3: case 4: case 5: case 6: case 7: case 8:
        return new MathCommand(comInd, line, realLine);
      case 9:
        return new NopCommand(line, realLine);
      case 10: case 11: case 12: case 13:
        return new JumpCommand(comInd, line, realLine);
      case 14:
        return new PrintCommand(line, realLine);
      case 1: case 15:
        return new NeedsOneMemCommand(comInd, line, realLine);
      default:
        return null;
    }
  }
  public static int toIntAbsolute(final String s){
		// BeCoz Integer#parseInt() is slow and try-catch is expensive.
    int result=0;
    for(final char c:s.toCharArray()){
      result+=c-48;
      result*=10;
    }
    return result/10;
  }
	public static String lineGen(final String[]line){
		return Arrays.toString(line).substring(1).replace(", ", " ").replace("]", "\n\n");
	}
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
  public static String convertUnicode(final String in){
    if(in.length()<6)return in;
    String temp=in;
    // Regex slow ._.
    for(int i=0;i<temp.length()-6;i++){
      if(temp.substring(i, i+2).toLowerCase().equals("uu")){
        boolean confirmed=true;
        for(int i2=i+2;i2<6;i2++)
          if(!Runner.isDigit(temp.charAt(i2)))confirmed=false;
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
  public static Command create(final String[] line, final String[] realLine, final ExecutorService executor,
  final Pattern jumps, final Pattern maths, final Pattern pwvar,
  final HashMap<String, Integer> binaryMap){
    final Command com=new Command(line, realLine, executor, jumps, maths, pwvar, binaryMap);
    com.compile();
    return com;
  }
}