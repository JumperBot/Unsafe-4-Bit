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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
  final ExecutorService threads=Executors.newFixedThreadPool(
    Runtime.getRuntime().availableProcessors()*2
  );
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
                          .append(input.substring(m2.start()+1)).toString();
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
		final ArrayList<int[]> list=new ArrayList<>();
    final Pattern jumps=Pattern.compile("j(m|l|e|ne)", Pattern.CASE_INSENSITIVE);
    final Pattern maths=Pattern.compile("add|sub|mul|div|r*mod", Pattern.CASE_INSENSITIVE);
    final Pattern pwvar=Pattern.compile("wvar|print", Pattern.CASE_INSENSITIVE);
		boolean cancelOptimization=false;
    final ArrayList<Command> commands=new ArrayList<>();
		for(final String arrTemp:arr){
      final String[] realTemp=divider.split(arrTemp);
      final ArrayList<String> tempList=new ArrayList<>();
      for(String s:realTemp){
        if(s.startsWith("\"")&&s.endsWith("\""))
          for(final String s2:convertToMem(s.substring(1, s.length()-1)))
            tempList.add(s2);
        else
          tempList.add(s);
      }
      final String[] temp=tempList.toArray(new String[tempList.size()]);
			if(temp.length<2&&temp.length>0&&!temp[0].equalsIgnoreCase("nop")){
        if(!(temp[0].equals("\n")||temp[0].trim().isEmpty()))
          warnings.append("Warning: |\n")
                  .append("    Command: |\n")
                  .append("        \"")
                  .append(temp[0])
                  .append("\" Will Be Ignored For It Has No Arguments: |\n")
                  .append("            ")
                  .append(lineGen(temp));
			}else
        commands.add(Command.create(temp, realTemp, threads, jumps, maths, pwvar, binaryMap));
		}
    threads.shutdown();
    threads.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
    for(final Command command:commands){
      errors.append(command.getErrors());
      if(command.cancelOptimization())cancelOptimization=true;
      list.add(command.getCompiled());
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
  private void writeToFile(final String outName, final ArrayList<int[]> list)throws Exception{
    final File outFile=new File(outName);
    outFile.createNewFile();
    try(final FileOutputStream stream=new FileOutputStream(outFile)){
      for(final int[] i:list)
        for(final int out:i)
          stream.write(out);
    }
  }
	private String lineGen(final String[]temp){
		return Arrays.toString(temp).substring(1).replace(", ", " ").replace("]", "\n\n");
	}
	public static String manPadding(final String str, final int i){
    return String.format(
      new StringBuilder("%").append(i).append("s").toString(),
      str
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
}
