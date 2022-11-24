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
import java.util.regex.Matcher;

class Command{
  final ArrayList<Integer> compiled=new ArrayList<>();
  final StringBuilder errors=new StringBuilder();
  final ExecutorService executor;
  final HashMap<String, Integer> binaryMap;
  final Pattern jumps;
  final Pattern maths;
  final Pattern pwvar;
  final Pattern unicode;
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
  final Pattern jumps, final Pattern maths, final Pattern pwvar, final Pattern unicode,
  final HashMap<String, Integer> binaryMap){
    this.line=line;
    this.realLine=realLine;
    this.executor=executor;
    this.binaryMap=binaryMap;
    this.jumps=jumps;
    this.maths=maths;
    this.pwvar=pwvar;
    this.unicode=unicode;
    this.line[0]=this.line[0].toLowerCase();
  }
  private void compile(){
    executor.execute(new Runnable(){
      public void run(){
        checkCases();
        if(errors.length()==0){
          final int index=binaryMap.get(line[0]);
          compiled.add(index);
          if(index>9&&index<14){
            final String[] args=new String[line.length-2];
            System.arraycopy(line, 1, args, 0, args.length);
            for(final String s:args)
              compiled.add(toIntAbsolute(s));
            final int lineNo=toIntAbsolute(line[line.length-1]);
            final String bin=UFBC.manPadding(Integer.toBinaryString(lineNo), 16);
            final String bin1=bin.substring(0, 8);
            if(!bin1.contains("0"))
              compiled.add(0);
            else
              compiled.add(Integer.parseInt(bin1, 2));
            compiled.add(Integer.parseInt(bin.substring(8), 2));
          }else{
            final String[] args=new String[line.length-1];
            System.arraycopy(line, 1, args, 0, args.length);
            if(index==0||index==14)
              compiled.add(args.length);
            for(final String s:args)
              compiled.add(toIntAbsolute(s));
          }
        }
      }
    });
  }
  private void checkCases(){
    boolean isCommand=true;
    if(line[0].equals("trim"))
      checkLength(line, 3);
    else if(line[0].equals("nvar")){
      if(!checkLength(line, 2))
        checkIfMemSafe(line, line[1]);
    }else if(line[0].equals("read")){
      if(!checkLength(line, 2)){
        checkIfMemSafe(line, line[1]);
        cancelOptimization=true;
      }
    }else if(jumps.matcher(line[0]).matches()){
      if(!checkLength(line, 4))
        for(byte i=1;i<3;i++)checkIfMem(line, line[i]);
    }else if(maths.matcher(line[0]).matches()){
      if(!checkLength(line, 3)){
        checkIfMemSafe(line, line[1]);
        for(byte i=1;i<3;i++)checkIfMem(line, line[i]);
      }
    }else if(pwvar.matcher(line[0]).matches()){
      if(line.length>256)
        error(
          realLine, "Command", line[0],
          "Has Too Many Arguments",
          lineGen(line)
        );
      else{
        if(line[0].startsWith("w"))
          checkIfMemSafe(line, line[1]);
        for(short i=2;i<line.length;i++)
          checkIfMem(line, line[i]);
      }
    }else if(line[0].equals("nop")) // One day I'll regret this.
      checkLength(line, 1);
    else{
      error(
        line, "Command", line[0],
        "Does Not Exist"
      );
      isCommand=false;
    }
    if(!line[0].equalsIgnoreCase("nop")&&isCommand)
      checkIfMem(line, line[1]);
  }
  private int toIntAbsolute(final String s){
    return toIntAbsolute(s.toCharArray());
  }
	private int toIntAbsolute(final char[] arr){
		// BeCoz Integer#parseInt() is slow and try-catch is expensive.
    int result=0;
    for(final char c:arr){
      result+=c-48;
      result*=10;
    }
    return result/10;
	}
	private void checkIfMem(final String[] line, final String s){
		try{
			if(Long.parseLong(s)>255)
				error(
					line, "Memory Index", s,
					"Is Larger Than 255 And Will Not Point To Memory"
				);
		}catch(final Exception e){
			error(
				line, "Memory Index Expected Instead Of", s,
				"Should Be Replaced With A Memory Index"
			);
		}
	}
	private void checkIfMemSafe(final String[] line, final String s){
		try{
			if(Long.parseLong(s)<38)
				error(
					line, "Memory Index", s,
					"Endangers A Read-Only Memory Index"
				);
		}catch(final Exception e){
			error(
				line, "Memory Index Expected Instead Of", s,
				"Should Be Replaced With A Memory Index"
			);
		}
	}
	// false if error is not thrown. Misleading eh?
	private boolean checkLength(final String[] line, final int length){
		if(line.length!=length){
			final String num=(length<1)?"Zero":(length==1)?"One":(length==2)?"Two":"Three";
			error(
				line, "Command", line[0],
				String.format("Needs No Less And No More Than %s Argument To Work", num)
			);
			return true;
		}
		return false;
	}
	private String lineGen(final String[]line){
		return Arrays.toString(line).substring(1).replace(", ", " ").replace("]", "\n\n");
	}
	private void error(final String[] line, final String... in){
    if(in.length<4)
      errors.append("Error: |\n")
            .append(String.format("    %s: |\n", in[0]))
            .append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
            .append(String.format("            %s", lineGen(line)));
    else
      errors.append("Error: |\n")
            .append(String.format("    %s: |\n", in[0]))
            .append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
            .append(String.format("            %s", convertUnicode(lineGen(line).replace("\n\n", "\n"))))
            .append(String.format("        %s: |\n", "Which Is When Converted"))
            .append(String.format("            %s", in[3]));
	}
  private String convertUnicode(final String in){
    if(in.length()<2)return in;
    String line=in;
    try{
      for(final Matcher m=unicode.matcher(line);m.find();m.reset(line)){
        line=new StringBuilder(line.substring(0, m.start()))
        .append((char)toIntAbsolute(
          line.substring(m.start()+2, m.end()).toCharArray()
        ))
        .append(line.substring(m.end())).toString();
      }
    }catch(final Exception e){}
    return line;
  }
  public static Command create(final String[] line, final String[] realLine, final ExecutorService executor,
  final Pattern jumps, final Pattern maths, final Pattern pwvar, final Pattern unicode,
  final HashMap<String, Integer> binaryMap){
    final Command com=new Command(line, realLine, executor, jumps, maths, pwvar, unicode, binaryMap);
    com.compile();
    return com;
  }
}