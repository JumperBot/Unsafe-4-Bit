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
	final static Pattern allowed=Pattern.compile("[^a-zA-Z0-9 \n-|,\t]");
	final static Pattern divider=Pattern.compile("[-|, \t]+");
	final static Pattern empties=Pattern.compile(" *\n+ *");
	final static Pattern comment=Pattern.compile("//.*\n*");
	final static Pattern morecom=Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
	final static StringBuilder errors=new StringBuilder();
	public static void main(final String[]a)throws Exception{
		final StringBuilder inBuilder=new StringBuilder();
		try(final BufferedReader scan=new BufferedReader(new FileReader(a[0].trim()))){
			String temp;
			while((temp=scan.readLine())!=null)inBuilder.append(temp).append("\n");
		}
		final String input=inBuilder.toString();
		final String[] arr=empties.split(
			divider.matcher(
				allowed.matcher(
					comment.matcher(
						morecom.matcher(
							input.toLowerCase()
						).replaceAll("\n")
					).replaceAll("\n")
				).replaceAll("")
			).replaceAll(" ")
		);
		final StringBuilder warnings=new StringBuilder();
		final ArrayList<String[]> list=new ArrayList<>();
		for(final String arrTemp:arr){
			final String[] temp=divider.split(arrTemp);
			if(temp.length<2&&temp.length>0&&!temp[0].equals("nop"))
				warnings.append("Warning: |\n")
								.append("    Command: |\n")
								.append("        \"")
								.append(temp[0])
								.append("\" Will Be Ignored For It Has No Arguments: |\n")
								.append("            ")
								.append(lineGen(temp));
			else{
				boolean isCommand=true;
				switch(temp[0]){
					case "trim":
						if(checkLength(temp, 3))break;
					case "nvar":
					case "read":
						if(!temp[0].startsWith("t")){
							if(checkLength(temp, 2))break;
							checkIfMemSafe(temp, temp[1]);
						}
						break;
					case "jm":
					case "jl":
					case "je":
					case "jne":
						if(checkLength(temp, 4))break;
					case "add":
					case "sub":
					case "mul":
					case "div":
					case "mod":
					case "rmod":
						if(!temp[0].startsWith("j")){
							if(checkLength(temp, 3))break;
							checkIfMemSafe(temp, temp[1]);
						}
						for(byte i=1;i<3;i++)checkIfMem(temp, temp[i]);
						break;
					case "wvar":
					case "print":
						if(temp.length>Byte.MAX_VALUE-1){
							error(
								temp, "Command", temp[0],
								"Has Too Many Arguments"
							);
							break;
						}
						if(temp[0].startsWith("w"))checkIfMemSafe(temp, temp[1]);
						for(byte i=2;i<temp.length;i++)checkIfMem(temp, temp[i]);
						break;
					case "nop":
						checkLength(temp, 1);
						break;
					default:
						error(
							temp, "Command", temp[0],
							"Does Not Exist"
						);
						isCommand=false;
						break;
				}
				if(!temp[0].equals("nop")&&isCommand)checkIfMem(temp, temp[1]);
				list.add(temp);
			}
		}
		final String ANSI_RESET="\u001B[0m";
		final String ANSI_BRIGHT_YELLOW="\u001B[93m";
		if(errors.length()!=0){
			final String ANSI_BRIGHT_RED="\u001B[91m";
			System.out.print(
				String.format("%s%s%s%s",
					ANSI_RESET, ANSI_BRIGHT_RED, errors.toString(), ANSI_RESET
				)
			);
			if(warnings.length()!=0)
				System.out.print(
					String.format("%s%s%s%s",
						ANSI_RESET, ANSI_BRIGHT_YELLOW, warnings.toString(), ANSI_RESET
					)
				);
			return;
		}
		if(warnings.length()!=0&&errors.length()==0)
			System.out.print(
				String.format("%s%s%s%s",
					ANSI_RESET, ANSI_BRIGHT_YELLOW, warnings.toString(), ANSI_RESET
				)
			);
		try{
			final File outFile=new File(a[0].substring(0, a[0].lastIndexOf("."))+".ufbb");
			outFile.createNewFile();
			try(final FileOutputStream stream=new FileOutputStream(outFile)){
				for(int i=0;i<list.size();i++){
					final String[] temp=list.get(i);
					stream.write(getBin(temp[0]));
					if(temp[0].equals("wvar")||temp[0].equals("print"))
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
		}catch(final Exception e){
			System.out.println(e.toString());
		}
	}
	private static void checkIfMem(final String[] temp, final String s){
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
	private static void checkIfMemSafe(final String[] temp, final String s){
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
	private static boolean checkLength(final String[] temp, final int length){
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
	private static String lineGen(final String[]temp){
		return Arrays.toString(temp).substring(1).replace(", ", " ").replace("]", "\n\n");
	}
	private static void error(final String[] temp, final String... in){
		errors.append("Error: |\n")
					.append(String.format("    %s: |\n", in[0]))
					.append(String.format("        \"%s\" %s: |\n", in[1], in[2]))
					.append(String.format("            %s", lineGen(temp)));
	}
	private static String manPadding(final String str, final int i){
    final StringBuilder reverse=new StringBuilder(str).reverse();
    while(reverse.length()<i)reverse.append("0");
    while(reverse.length()>i)reverse.delete(0, 1);
    return reverse.reverse().toString();
  }
	final static HashMap<String, Integer> binaryMap=new HashMap<>(){{
		put("wvar" , 0 );
		put("nvar" , 1 );
		put("trim" , 2 );
		put("add"  , 3 );
		put("sub"  , 4 );
		put("mul"  , 5 );
		put("div"  , 6 );
		put("mod"  , 7 );
		put("rmod" , 8 );
		put("nop"  , 9 );
		put("jm"   , 10);
		put("jl"   , 11);
		put("je"   , 12);
		put("jne"  , 13);
		put("print", 14);
		put("read" , 15);
	}};
	private static int getBin(final String com){
		return binaryMap.get(com.trim());
	}
}
