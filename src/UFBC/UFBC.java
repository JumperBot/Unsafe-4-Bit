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
		final StringBuilder errors=new StringBuilder();
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
						if(temp.length!=3)
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than Two Arguments To Work", temp
							);
					case "nvar":
					case "read":
						if(temp.length!=2&&!temp[0].startsWith("t")){
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than One Argument To Work", temp
							);
							break;
						}
						try{
							if(Long.parseLong(temp[1])<38){
								error(
									errors, "Memory Index", temp[1],
									"Endangers A Read-Only Memory Index", temp
								);
							}
						}catch(final Exception e){
							try{
								error(
									errors, "Memory Index Expected Instead Of", temp[1],
									"Should Be Replaced With A Memory Index", temp
								);
							}catch(final Exception e2){}
						}
						break;
					case "jm":
					case "jl":
					case "je":
					case "jne":
						if(temp.length!=4)
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than Three Arguments To Work", temp
							);
					case "add":
					case "sub":
					case "mul":
					case "div":
					case "mod":
					case "rmod":
						if(!temp[0].startsWith("j")){
							if(temp.length!=3){
								error(
									errors, "Command", temp[0],
									"Needs No Less And No More Than Two Arguments To Work", temp
								);
								break;
							}
							try{
								if(Long.parseLong(temp[1])<38)
									error(
										errors, "Memory Index", temp[1],
										"Endangers A Read-Only Memory Index", temp
									);
							}catch(final Exception e){
								error(
									errors, "Memory Index Expected Instead Of", temp[1],
									"Should Be Replaced With A Memory Index", temp
								);
							}
						}
						for(byte i=1;i<3;i++){
							try{
								if(Long.parseLong(temp[i])>255)
									error(
										errors, "Memory Index", temp[i],
										"Is Larger Than 255 And Will Not Point To Memory", temp
									);
							}catch(final Exception e){
								error(
									errors, "Memory Index Expected Instead Of", temp[1],
									"Should Be Replaced With A Memory Index", temp
								);
							}
						}
						break;
					case "wvar":
					case "print":
						if(temp.length>Byte.MAX_VALUE-1){
							error(
								errors, "Command", temp[0],
								"Has Too Many Arguments", temp
							);
							break;
						}
						if(temp[0].startsWith("w")){
							try{
								if(Long.parseLong(temp[1])<38)
									error(
										errors, "Memory Index", temp[1],
										"Endangers A Read-Only Memory Index", temp
									);
							}catch(final Exception e){
								error(
									errors, "Memory Index Expected Instead Of", temp[1],
									"Should Be Replaced With A Memory Index", temp
								);
							}
						}
						for(byte i=2;i<temp.length;i++){
							try{
								if(Long.parseLong(temp[i])>255)
									error(
										errors, "Memory Index", temp[i],
										"Is Larger Than 255 And Will Not Point To Memory", temp
									);
							}catch(final Exception e){
								error(
									errors, "Memory Index Expected Instead Of", temp[i],
									"Should Be Replaced With A Memory Index", temp
								);
							}
						}
						break;
					case "nop":
						if(temp.length!=1)
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than Zero Arguments", temp
							);
						break;
					default:
						error(
							errors, "Command", temp[0],
							"Does Not Exist", temp
						);
						isCommand=false;
						break;
				}
				if(!temp[0].equals("nop")&&isCommand){
					try{
						if(Long.parseLong(temp[1])>255)
							error(
								errors, "Memory Index", temp[1],
								"Is Larger Than 255 And Will Not Point To Memory", temp
							);
					}catch(final Exception e){
						error(
							errors, "Memory Index Expected Instead Of", temp[1],
							"Should Be Replaced With A Memory Index", temp
						);
					}
				}
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
	private static String lineGen(final String[]temp){
		return Arrays.toString(temp).substring(1).replace(", ", " ").replace("]", "\n\n");
	}
	private static void error(final StringBuilder errors,
	final String in1, final String in2, final String in3, final String[] temp){
		errors.append("Error: |\n")
					.append(String.format("    %s: |\n", in1))
					.append(String.format("        \"%s\" %s: |\n", in2, in3))
					.append(String.format("            %s", lineGen(temp)));
	}
	private static String manPadding(final String str, final int i){
    final StringBuilder reverse=new StringBuilder(str).reverse();
    while(reverse.length()<i)reverse.append("0");
    while(reverse.length()>i)reverse.delete(0, 1);
    return reverse.reverse().toString();
  }
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
	private static int getBin(final String com){
		switch(com.trim()){
			case "wvar":
				return 0;
			case "nvar":
				return 1;
			case "trim":
				return 2;
			case "add":
				return 3;
			case "sub":
				return 4;
			case "mul":
				return 5;
			case "div":
				return 6;
			case "mod":
				return 7;
			case "rmod":
				return 8;
			case "nop":
				return 9;
			case "jm":
				return 10;
			case "jl":
				return 11;
			case "je":
				return 12;
			case "jne":
				return 13;
			case "print":
				return 14;
			case "read":
				return 15;
		}
		return 0;
	}
}
