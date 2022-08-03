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
	final static Pattern pat1=Pattern.compile("[^a-zA-Z0-9 \n-|,]");
	final static Pattern pat2=Pattern.compile("[-|, ]+");
	final static Pattern pat3=Pattern.compile(" *\n+ *");
	final static Pattern pat4=Pattern.compile("[a-zA-Z\u0000]+");
	public static void main(final String[]a){	
		final String input=
		new StringBuilder("wvar 38, 8 5 12 12 15 0 23 15 18 12 4 28 28 28 37\n")
							.append("trim 38 14\n")
							.append("add 50 0\n")
							.append("sub 50 0\n")
							.append("mul 50 0\n")
							.append("div 50 0\n")
							.append("mod 50 0\n")
							.append("rmod 50 0\n")
							.append("nop\n")
							.append("jm 0 0 0\n")
							.append("jl 0 0 0\n")
							.append("je 0 0 0\n")
							.append("jne 0 0 0\n")
							.append("print 0\n")
							.append("read 150\n")
							.append("nvar 38\n")
		.toString();
		final long start=System.currentTimeMillis();
		final String[] arr=pat3.split(
			pat2.matcher(
				pat1.matcher(input).replaceAll("")
			).replaceAll(" ")
		);
		final StringBuilder warnings=new StringBuilder();
		final StringBuilder errors=new StringBuilder();
		final ArrayList<String> list=new ArrayList<>();
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
			}
		}
		System.out.println("Compilation took: "+(System.currentTimeMillis()-start)+"ms\n");
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
		// For Debugging Only
		System.out.println(Arrays.toString(arr));
		System.out.println(Arrays.toString(list.toArray()));
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
}
