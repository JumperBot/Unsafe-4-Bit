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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UFBC{
	/**
	 * 0	-	0000	-	wvar	|	1		-	0001	-	nvar
	 * 2	-	0010	-	trim	|	3		-	0011	-	add
	 * 4	-	0100	-	sub		| 5		-	0101	-	mul
	 * 6	-	0110	-	div		| 7		-	0111	-	mod
	 * 8	-	1000	-	rmod	|	9		-	1001	-	nop
	 * 10	-	1010	-	jm		|	11	-	1011	-	jl
	 * 12	-	1100	-	je		|	13	-	1101	-	jne
	 * 14	-	1110	-	print	|	15	-	1111	-	read
	 **/
	final Pattern divider=Pattern.compile("[-|, \t]+");
	final Pattern empties=Pattern.compile(" *\n+ *");
	final Pattern comment=Pattern.compile("//.*\n*");
	final Pattern morecom=Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
	final StringBuilder errors=new StringBuilder();
  public UFBC(){}
	public void compile(final String fileName, final boolean recompile)throws Exception{
		final StringBuilder inBuilder=new StringBuilder();
		try(final BufferedReader scan=new BufferedReader(new FileReader(fileName))){
			String temp;
			while((temp=scan.readLine())!=null)inBuilder.append(temp).append("\n");
		}
		String input=inBuilder.toString();
		final Pattern dividerInString=Pattern.compile("\".*([-|, \t]).*\"");
    try{
      for(final Matcher m=dividerInString.matcher(input);m.find();)
        for(final Matcher m2=divider.matcher(input);m2.find(m.start());){
          input=new StringBuilder(input.substring(0, m2.start()))
                          .append("UU")
                          .append(manPadding(Integer.toString(m2.group().charAt(0)+0), 4))
                          .append(input.substring(m2.end())).toString();
          m.reset(input).find();
          m2.reset(input);
        }
    }catch(final Exception e){}
    final String[] arr=empties.split(
			divider.matcher(
        comment.matcher(
          morecom.matcher(
            input
          ).replaceAll("\n")
        ).replaceAll("\n")
			).replaceAll(" ")
		);
		final StringBuilder warnings=new StringBuilder();
		final ArrayList<String[]> list=new ArrayList<>();
    final Pattern jumps=Pattern.compile("j(m|l|e|ne)", Pattern.CASE_INSENSITIVE);
    final Pattern maths=Pattern.compile("add|sub|mul|div|r*mod", Pattern.CASE_INSENSITIVE);
    final Pattern pwvar=Pattern.compile("wvar|print", Pattern.CASE_INSENSITIVE);
		boolean cancelOptimization=false;
		for(final String arrTemp:arr){
      final String[] realTemp=divider.split(arrTemp);
      final ArrayList<String> tempList=new ArrayList<>();
      boolean startString=false;
      System.out.println(Arrays.toString(realTemp));
      for(String s:realTemp){
        if(s.startsWith("\"")){
          startString=true;
          for(final String s2:convertToMem(s.substring(1)))
            tempList.add(s2);
          if(s.endsWith("\""))
            startString=false;
          else
            tempList.add("0");
        }else if(s.endsWith("\"")){
          startString=false;
          for(final String s2:convertToMem(s.substring(0, s.length()-1)))
            tempList.add(s2);
        }else if(startString){
          for(final String s2:convertToMem(s))
            tempList.add(s2);
          tempList.add("0");
        }else{
          tempList.add(s);
        }
      }
      final String[] temp=tempList.toArray(new String[tempList.size()]);
			if(temp.length<2&&temp.length>0&&!temp[0].equalsIgnoreCase("nop")){
        if(!temp[0].equals("\n"))
          warnings.append("Warning: |\n")
                  .append("    Command: |\n")
                  .append("        \"")
                  .append(temp[0])
                  .append("\" Will Be Ignored For It Has No Arguments: |\n")
                  .append("            ")
                  .append(lineGen(temp));
			}else{
				boolean isCommand=true;
        if(temp[0].equalsIgnoreCase("trim"))
          checkLength(temp, 3);
        else if(temp[0].equalsIgnoreCase("nvar")){
          if(!checkLength(temp, 2))
            checkIfMemSafe(temp, temp[1]);
        }else if(temp[0].equalsIgnoreCase("read")){
          if(!checkLength(temp, 2)){
            checkIfMemSafe(temp, temp[1]);
            cancelOptimization=true;
          }
        }else if(jumps.matcher(temp[0]).matches()){
          if(!checkLength(temp, 4))
            for(int i=1;i<3;i++)checkIfMem(temp, temp[i]);
        }else if(maths.matcher(temp[0]).matches()){
          if(!checkLength(temp, 3)){
            checkIfMemSafe(temp, temp[1]);
            for(int i=1;i<3;i++)checkIfMem(temp, temp[i]);
          }
        }else if(pwvar.matcher(temp[0]).matches()){
          if(temp.length>255)
            error(
              realTemp, "Command", temp[0],
              "Has Too Many Arguments",
              lineGen(temp)
            );
          else{
						if(temp[0].startsWith("w"))
              checkIfMemSafe(temp, temp[1]);
						for(int i=2;i<temp.length;i++)
              checkIfMem(temp, temp[i]);
          }
        }else if(temp[0].equalsIgnoreCase("nop")) // One day I'll regret this.
          checkLength(temp, 1);
        else{
          error(
            temp, "Command", temp[0],
            "Does Not Exist"
          );
          isCommand=false;
				}
				if(!temp[0].equalsIgnoreCase("nop")&&isCommand)
          checkIfMem(temp, temp[1]);
				list.add(temp);
			}
		}
		final String ANSI_RESET="\u001B[0m";
    if(warnings.length()!=0)
      System.out.printf("%s%s%s%s\n",
        ANSI_RESET, "\u001B[93m", warnings.toString(), ANSI_RESET
      );
		if(errors.length()!=0){
      System.out.printf("%s%s%s%s\n",
        ANSI_RESET, "\u001B[91m", errors.toString(), ANSI_RESET
			);
			return;
		}
		final String outName=fileName.substring(0, fileName.lastIndexOf("."))+".ufbb";
		try{
      writeToFile(outName, list);
		}catch(final Exception e){
			System.out.println(e.toString());
		}
		if(cancelOptimization)
      System.out.println("Code cannot be optimized, but compilation is a success!");
    else if(recompile)
      new Runner(outName, false, false, false, false).runOptimized();
	}
  private String[] convertToMem(final String in){
    final ArrayList<String> mems=new ArrayList<>();
    boolean backSlash=false;
    for(final char c:in.toCharArray()){
      if(memMap.containsKey(c))
        mems.add(memMap.get(c).toString());
      else{
        if(c=='\\'){
          if(backSlash){
            backSlash=false;
            mems.add("21");
            mems.add("21");
            for(final char c2:manPadding(Integer.toString('\\'), 4).toCharArray())
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
            for(final char c2:manPadding(Integer.toString((int)c), 4).toCharArray())
              mems.add(memMap.get(c2).toString());
          }
          backSlash=false;
        }else{
          mems.add("21");
          mems.add("21");
          for(final char c2:manPadding(Integer.toString((int)c), 4).toCharArray())
            mems.add(memMap.get(c2).toString());
        }
      }
    }
    return mems.toArray(new String[mems.size()]);
  }
  final HashMap<Character, Integer> memMap=new HashMap<>(){{
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
  private void writeToFile(final String outName, final ArrayList<String[]> list)throws Exception{
    System.out.println(Arrays.deepToString(list.toArray()));
    final File outFile=new File(outName);
    outFile.createNewFile();
    try(final FileOutputStream stream=new FileOutputStream(outFile)){
      for(int i=0;i<list.size();i++){
        final String[] temp=list.get(i);
        stream.write(getBin(temp[0]));
        if(temp[0].equalsIgnoreCase("wvar")||temp[0].equalsIgnoreCase("print"))
          stream.write(temp.length-1);
        if(temp[0].startsWith("j")){
          for(int j=1;j<temp.length-1;j++)
            stream.write(Integer.parseInt(temp[j]));
          final int line=Integer.parseInt(temp[temp.length-1]);
          final String bin=manPadding(Integer.toBinaryString(line), 16);
          final String bin1=bin.substring(0, 8);
          if(!bin1.contains("0"))
            stream.write(0);
          else
            stream.write(Integer.parseInt(bin1, 2));
          stream.write(Integer.parseInt(bin.substring(8), 2));
        }else
          for(int j=1;j<temp.length;j++)
            stream.write(Integer.parseInt(temp[j]));
      }
    }
  }
	private void checkIfMem(final String[] temp, final String s){
		try{
			if(Long.parseLong(s)>255)
				error(
					temp, "Memory Index", s,
					"Is Larger Than 255 And Will Not Point To Memory"
				);
		}catch(final Exception e){
			error(
				temp, "Memory Index Expected Instead Of", s,
				"Should Be Replaced With A Memory Index"
			);
		}
	}
	private void checkIfMemSafe(final String[] temp, final String s){
		try{
			if(Long.parseLong(s)<38)
				error(
					temp, "Memory Index", s,
					"Endangers A Read-Only Memory Index"
				);
		}catch(final Exception e){
			error(
				temp, "Memory Index Expected Instead Of", s,
				"Should Be Replaced With A Memory Index"
			);
		}
	}
	// false if error is not thrown. Misleading eh?
	private boolean checkLength(final String[] temp, final int length){
		if(temp.length!=length){
			final String num=(length<1)?"Zero":(length==1)?"One":(length==2)?"Two":"Three";
			error(
				temp, "Command", temp[0],
				String.format("Needs No Less And No More Than %s Argument To Work", num)
			);
			return true;
		}
		return false;
	}
	private String lineGen(final String[]temp){
		return Arrays.toString(temp).substring(1).replace(", ", " ").replace("]", "\n\n");
	}
	private void error(final String[] temp, final String... in){
    if(in.length<4)
      errors.append("Error: |\n")
            .append(String.format("    %s: |\n", in[0]))
            .append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
            .append(String.format("            %s", lineGen(temp)));
    else
      errors.append("Error: |\n")
            .append(String.format("    %s: |\n", in[0]))
            .append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
            .append(String.format("            %s", convertUnicode(lineGen(temp).replace("\n\n", "\n"))))
            .append(String.format("        %s: |\n", "Which Is When Converted"))
            .append(String.format("            %s", in[3]));
	}
	private long toLongAbsolute(final char[] arr){
		// BeCoz Long#parseLong() is slow and try-catch is expensive.
    long result=0;
    for(final char c:arr){
      result+=c-48;
      result*=10;
    }
    return result/10;
	}
  final Pattern unicode=Pattern.compile("(uu|UU)(\\d{1,4})");
  private String convertUnicode(final String in){
    if(in.length()<2)return in;
    String temp=in;
    try{
      for(final Matcher m=unicode.matcher(temp);m.find();m.reset(temp)){
        temp=new StringBuilder(temp.substring(0, m.start()))
        .append((char)toLongAbsolute(
          temp.substring(m.start()+2, m.end()).toCharArray()
        ))
        .append(temp.substring(m.end())).toString();
      }
    }catch(final Exception e){}
    return temp;
  }
	private String manPadding(final String str, final int i){
    return String.format(
      "%"+i+"s", str
    ).replace(" ", "0");
  }
	final HashMap<String, Integer> binaryMap=new HashMap<>(){{
		final String[] commands={
      "wvar", "nvar", "trim",
      "add", "sub", "mul", "div", "mod", "rmod",
      "nop",
      "jm", "jl", "je", "jne",
      "print", "read"
    };
    for(int i=0;i<commands.length;i++){
      put(commands[i], i);
    }
	}};
	private int getBin(final String com){
		return binaryMap.get(com.trim());
	}
}
