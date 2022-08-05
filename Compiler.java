import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.math.BigInteger;

import java.util.Arrays;
import java.util.ArrayList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Compiler{
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
	final static Pattern pat1=Pattern.compile("[^a-zA-Z0-9 \n-|,\t]");
	final static Pattern pat2=Pattern.compile("[-|, \t]+");
	final static Pattern pat3=Pattern.compile(" *\n+ *");
	final static Pattern pat4=Pattern.compile("[a-zA-Z\u0000]+");
	public static void main(final String[]a)throws Exception{
		final StringBuilder inBuilder=new StringBuilder();
		try(final BufferedReader scan=new BufferedReader(new FileReader(a[0].trim()))){
			String temp;
			while((temp=scan.readLine())!=null)inBuilder.append(temp).append("\n");
		}
		final String input=inBuilder.toString();
		final String[] arr=pat3.split(
			pat2.matcher(
				pat1.matcher(input).replaceAll("")
			).replaceAll(" ")
		);
		final StringBuilder warnings=new StringBuilder();
		final StringBuilder errors=new StringBuilder();
		final ArrayList<String[]> list=new ArrayList<>();
		for(final String arrTemp:arr){
			final String[] temp=pat2.split(arrTemp);
			if(temp.length<2&&temp.length>0&&!temp[0].equals("nop"))
				warnings.append("Warning: |\n")
								.append("    Command: |\n")
								.append("        \"")
								.append(temp[0])
								.append("\" Will Be Ignored For It Has No Arguments: |\n")
								.append("            ")
								.append(lineGen(temp));
			else{
				boolean oneMem=false;
				boolean twoMem=false;
				boolean infMem=false;
				// Command Checker.
				if(temp[0].startsWith("j"))
					switch(temp[0]){
						case "jm":
							twoMem=true;
							break;
						case "jl":
							twoMem=true;
							break;
						case "je":
							twoMem=true;
							break;
						case "jne":
							twoMem=true;
							break;
						default:
							error(
								errors, "Command", temp[0],
								"Does Not Exist", temp
							);
							break;
					}
				else
					switch(temp[0]){
						case "nvar":
							oneMem=true;
							break;
						case "trim":
							oneMem=true;
							break;
						case "read":
							oneMem=true;
							break;
						case "add":
							twoMem=true;
							break;
						case "sub":
							twoMem=true;
							break;
						case "mul":
							twoMem=true;
							break;
						case "div":
							twoMem=true;
							break;
						case "mod":
							twoMem=true;
							break;
						case "rmod":
							twoMem=true;
							break;
						case "wvar":
							infMem=true;
							break;
						case "print":
							infMem=true;
							break;
						case "nop":
							// LMAO Just Do Nothing
							break;
						default:
							error(
								errors, "Command", temp[0],
								"Does Not Exist", temp
							);
							break;
					}
				// Argument Checker.
				if(oneMem||twoMem||infMem){
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
				if(oneMem){
					if(temp[0].startsWith("j")){
						if(temp.length!=3)
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than Two Arguments To Work", temp
							);
					}else{
						if(temp[0].equals("trim")&&temp.length!=3)
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than Two Arguments To Work", temp
							);
						else if(!temp[0].equals("trim")&&temp.length!=2)
							error(
								errors, "Command", temp[0],
								"Needs No Less And No More Than One Argument To Work", temp
							);
						else{
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
						}
					}
				}else if(twoMem){
					if(temp[0].startsWith("j")&&temp.length!=4)
						error(
							errors, "Command", temp[0],
							"Needs No Less And No More Than Three Arguments To Work", temp
						);
					else if(!temp[0].startsWith("j")&&temp.length!=3)
						error(
							errors, "Command", temp[0],
							"Needs No Less And No More Than Two Arguments To Work", temp
						);
					else if(!temp[0].startsWith("j")&&
					(temp[0].equals("add")||temp[0].equals("sub")||
					 temp[0].equals("mul")||temp[0].equals("div")||
					 temp[0].equals("mod")||temp[0].equals("rmod"))){
						try{
							final long memArg=Long.parseLong(temp[1]);
							if(Long.parseLong(temp[1])<38)
								error(
									errors, "Memory Index", temp[1],
									"Endangers A Read-Only Memory Index", temp
								);
							else if(memArg>255)
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
						try{
							if(Long.parseLong(temp[2])>255)
								error(
									errors, "Memory Index", temp[2],
									"Is Larger Than 255 And Will Not Point To Memory", temp
								);
						}catch(final Exception e){
							error(
								errors, "Memory Index Expected Instead Of", temp[2],
								"Should Be Replaced With A Memory Index", temp
							);
						}
					}else{
						try{
							if(Long.parseLong(temp[2])>255)
								error(
									errors, "Memory Index", temp[2],
									"Is Larger Than 255 And Will Not Point To Memory", temp
								);
						}catch(final Exception e){
							error(
								errors, "Memory Index Expected Instead Of", temp[2],
								"Should Be Replaced With A Memory Index", temp
							);
						}
					}
				}else if(infMem){
					if(temp.length>Byte.MAX_VALUE-1)
						error(
							errors, "Command", temp[0],
							"Has Too Many Arguments", temp
						);
					else{
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
					}
				}else if(temp[0].equals("nop")&&temp.length!=1){
					error(
						errors, "Command", temp[0],
						"Needs No Less And No More Than Zero Arguments", temp
					);
				}
				list.add(temp);
			}
		}
		// Warnings && Errors.
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
		final StringBuilder bin=new StringBuilder();
		for(int i=0;i<list.size();i++){
			final String[] temp=list.get(i);
			bin.append(getBin(temp[0]));
			if(temp[0].equals("wvar")||temp[0].equals("print"))
				bin.append(manPadding(Integer.toBinaryString(temp.length-1), 8));
			for(int j=1;j<temp.length;j++)
				bin.append(manPadding(Integer.toBinaryString(Integer.parseInt(temp[j])), 8));
		}
		while(bin.length()%8!=0)bin.append("111000000000");
		final byte[] bytes=new BigInteger(bin.toString(), 2).toByteArray();
		try{
			final File outFile=new File("Output.ufbb");
			outFile.createNewFile();
			try(final FileOutputStream stream=new FileOutputStream(outFile)){
				stream.write(bytes);
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
    while(reverse.length()<i)
      reverse.append("0");
    while(reverse.length()>i)
      reverse.delete(0, 1);
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
	private static String getBin(final String com){
		switch(com){
			case "wvar":
				return "0000";
			case "nvar":
				return "0001";
			case "trim":
				return "0010";
			case "add":
				return "0011";
			case "sub":
				return "0100";
			case "mul":
				return "0101";
			case "div":
				return "0110";
			case "mod":
				return "0111";
			case "rmod":
				return "1000";
			case "nop":
				return "1001";
			case "jm":
				return "1010";
			case "jl":
				return "1011";
			case "je":
				return "1100";
			case "jne":
				return "1101";
			case "print":
				return "1110";
			case "read":
				return "1111";
		}
		return null;
	}
}
