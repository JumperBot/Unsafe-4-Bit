import java.util.Arrays;

import java.util.regex.Pattern;

class Main{
	/**
	 * 0	-	0000	-	wvar	|	1		-	0001	-	nvar
	 * 2	-	0010	-	rvar	|	3		-	0011	-	add
	 * 4	-	0100	-	sub		| 5		-	0101	-	mul
	 * 6	-	0110	-	div		| 7		-	0111	-	mod
	 * 8	-	1000	-	jm		|	9		-	1001	-	jl
	 * 10	-	1010	-	jz		|	11	-	1011	-	jo
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
							.append("print 38\n")
							.append("trim 38, 28 27\n")
							.append("print 38\n")
							.append("wvar 38, 38\n")
							.append("print 37 38\n")
							.append("print 37 38\n")
							.append("trim 38, 32\n")
							.append("print 37 38\n")
							.append("print 37 38\n")
							.append("nvar 38\n")
							.append("jm 27\n")
		.toString();
		final String[] arr=pat3.split(
			pat2.matcher(
				pat1.matcher(input).replaceAll("")
			).replaceAll(" ")
		);
		final char[] mem=new char[256];
		mem[0]=' ';
		for(byte i=0;(char)(i-1+'A')!='Z';i++)
			mem[i+1]=(char)(i+'A');
		for(byte i=0;i<10;i++)
			mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
		for(short i=38;i<mem.length;i++)
			mem[i]='\u0000';
		final short[] memInd=new short[256];
		for(short i=0;i<memInd.length;i++)
			memInd[i]=0;
		for(int i=0;i<arr.length;i++){
			final String[] temp=pat2.split(arr[i]);
			if(temp.length>1){
				if(!gate(temp, mem, memInd))
					return;
				switch(temp[0]){
					case "wvar":
						wvar(temp, mem, memInd);
						break;
					case "nvar":
						nvar(temp, mem, memInd);
						break;
					case "trim":
						trim(temp, mem, memInd);
						break;
					case "add":
						break;
					case "sub":
						break;
					case "mul":
						break;
					case "div":
						break;
					case "mod":
						break;
					case "jm":
						if(true){
							final StringBuilder builder=new StringBuilder();
							for(short j=1;j<temp.length;j++)
								builder.append(readMem(Short.parseShort(temp[j]), mem, memInd));
							i=Integer.parseInt(builder.toString());
						}
						break;
					case "jl":
						break;
					case "jz":
						break;
					case "jo":
						break;
					case "je":
						break;
					case "jne":
						break;
					case "print":
						print(temp, mem, memInd);
						break;
					case "read":
						break;
					default:
						System.out.println(
							new StringBuilder("\nUnknown Command: ").append(temp[0]).toString()
						);
						return;
				}
			}
		}
		System.out.println(
			new StringBuilder("\n")
								.append(Arrays.toString(mem))
								.append("\n")
								.append(Arrays.toString(memInd))
								.toString()
		);
		for(int i=0;i<memInd.length;i++)
			if(memInd[i]!=0)
				System.out.println(
					new StringBuilder("\nMemory Leak At Index: ")
										.append(String.valueOf(i))
										.toString()
				);
	}
	private static void print(final String[] temp, final char[] mem, final short[] memInd){
		final StringBuilder builder=new StringBuilder();
		for(short i=1;i<temp.length;i++)
			builder.append(readMem(Short.parseShort(temp[i]), mem, memInd));
		System.out.print(builder.toString());
	}
	private static boolean gate(final String[] temp, final char[] mem, final short[] memInd){
		final boolean isNumGuzzler=(
			temp[0].equals("trim")||temp[0]=="trim"||
			temp[0].equals("jm")	||temp[0]=="jm"
		);
		if(temp[0].equals("print")||temp[0]=="print")
			return true;
		for(int i=1;i<temp.length;i++){
			if(pat4.matcher(temp[i]).replaceAll("").length()!=temp[i].length()){
				System.out.println(
					new StringBuilder("\nMemory Index Expected Instead Of: ")
										.append(temp[i])
										.toString()
				);
				return false;
			}
			final short ind=Short.parseShort(temp[i]);
			if(ind>255){
				System.out.println(
					new StringBuilder("\nNonExistent Memory Index: ")
										.append(temp[i])
										.toString()
				);
				return false;
			}
			if(i==1&&ind<38&&!(temp[0].equals("jm")||temp[0]=="jm")){
				System.out.println(
					new StringBuilder("\nROM: Can't Write On Memory Index: ")
										.append(temp[i])
										.toString()
				);
				return false;
			}
			if(i>1&&isNumGuzzler){
				final String read=readMem(ind, mem, memInd);
				if(pat4.matcher(read).replaceAll("").length()!=read.length()){
					System.out.println(
						new StringBuilder("\nTrim: Int Value Expected On Memory Index: ")
											.append(temp[i])
											.append(" Instead Of: ")
											.append(read)
											.toString()
					);
					return false;
				}
			}
		}
		return true;
	}
	private static void wvar(final String[] temp, final char[] mem, final short[] memInd){
		short ind=Short.parseShort(temp[1]);
		final short tempInd=ind;
		for(int i=2;i<temp.length;i++)
			for(final char c:readMem(Short.parseShort(temp[i]), mem, memInd).toCharArray()){
				mem[ind]=c;
				ind++;
			}
		memInd[tempInd]=ind;
	}
	private static void nvar(final String[] temp, final char[] mem, final short[] memInd){
		final short ind=Short.parseShort(temp[1]);
		for(short i=ind;i-1<memInd[ind];i++)
			if(memInd[i]==0||i==ind)
				mem[i]='\u0000';
		memInd[ind]=0;
	}
	private static void trim(final String[] temp, final char[] mem, final short[] memInd){
		final short ind=Short.parseShort(temp[1]);
		final StringBuilder builder=new StringBuilder();
		for(int i=2;i<temp.length;i++)
			builder.append(readMem(Short.parseShort(temp[i]), mem, memInd));
		final short max=Short.parseShort(builder.toString());
		if(memInd[ind]>ind+max-1){
			final String read=readMem(ind, mem, memInd);
			nvar(temp, mem, memInd);
			for(int i=0;i<max;i++){
				mem[ind+i]=read.charAt(i);
			}
			memInd[ind]=(short)(ind+max-1);
		}
	}
	private static String readMem(final short ind, final char[] mem, final short[] memInd){
		final StringBuilder builder=new StringBuilder();
		if(memInd[ind]==0)
			return Character.toString(mem[ind]);
		for(short i=ind;i<memInd[ind]+1;i++)
			builder.append(mem[i]);
		return builder.toString();
	}
}
