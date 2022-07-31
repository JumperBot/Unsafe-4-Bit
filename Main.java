import java.util.ArrayList;
import java.util.Arrays;

import java.util.regex.Pattern;

class Main{
	/**
	 * 0	-	0000	-	wvar
	 * 1	-	0001	-	nvar
	 * 2	-	0010	-	rvar
	 * 3	-	0011	-	add
	 * 4	-	0100	-	sub
	 * 5	-	0101	-	mul
	 * 6	-	0110	-	div
	 * 7	-	0111	-	mod
	 * 8	-	1000	-	jm
	 * 9	-	1001	-	jl
	 * 10	-	1010	-	jz
	 * 11	-	1011	-	jo
	 * 12	-	1100	-	je
	 * 13	-	1101	-	jne
	 * 14	-	1110	-	print
	 * 15	-	1111	-	read
	 **/
	final static Pattern pat1=Pattern.compile("[^a-zA-Z0-9 \n-|,]");
	final static Pattern pat2=Pattern.compile("[-|, ]+");
	final static Pattern pat3=Pattern.compile(" *\n+ *");
	final static Pattern pat4=Pattern.compile("[a-zA-Z\u0000]+");
	public static void main(final String[]a){
		final String input=
		new StringBuilder("wvar 38, 8 5 12 12 15 0 23 15 18 12 4 28 28 28 37\n")
							.append("print 38\n")
							.append("nvar 38")
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
			memInd[i]=i;
		for(final String s:arr){
			final String[] temp=pat2.split(s);
			if(temp.length>1){
				try{
					final short sh=Short.parseShort(temp[1]);
				}catch(Exception e){
					System.out.println(
						new StringBuilder("\nMemory Index Expected Instead Of: ")
											.append(temp[1])
											.toString()
					);
					return;
				}
				switch(temp[0]){
					case "wvar":
						if(!wvar(temp, mem, memInd))
							return;
						break;
					case "nvar":
						nvar(temp, mem, memInd);
						break;
					case "trim":
						if(!trim(temp, mem, memInd))
							return;
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
						for(short i=1;i<temp.length;i++)
							System.out.print(readMem(Short.parseShort(temp[1]), mem, memInd));
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
		for(int i=0;i<memInd.length;i++)
			if(memInd[i]!=i)
				System.out.println(
					new StringBuilder("\nMemory Leak At Index: ")
										.append(String.valueOf(i))
										.toString()
				);
		System.out.println(Arrays.toString(arr));
		System.out.println(Arrays.toString(mem));
		System.out.println(Arrays.toString(memInd));
	}
	private static boolean wvar(final String[] temp, final char[] mem, final short[] memInd){
		short ind=Short.parseShort(temp[1]);
		if(ind>255){
			System.out.println(
				new StringBuilder("\nNonexistent Memory Index: ").append(temp[1]).toString()
			);
			return false;
		}
		if(ind>37){
			final short tempInd=ind;
			for(int i=2;i<temp.length;i++)
				for(final char c:readMem(Short.parseShort(temp[i]), mem, memInd).toCharArray()){
					System.out.println(Character.toString(c));
					mem[ind]=c;
					ind++;
				}
			memInd[tempInd]=ind;
			return true;
		}
		System.out.println(
			new StringBuilder("\nROM: Can't Write On Memory Index: ")
								.append(temp[1])
								.toString()
		);
		return false;
	}
	private static void nvar(final String[] temp, final char[] mem, final short[] memInd){
		final short index=Short.parseShort(temp[1]);
		for(short i=index;i<memInd[index];i++)
			mem[i]='\u0000';
		memInd[index]=index;
	}
	private static boolean trim(final String[] temp, final char[] mem, final short[] memInd){
		final short index=Short.parseShort(temp[1]);
		final StringBuilder builder=new StringBuilder();
		for(byte i=2;i<temp.length;i++)
			for(short j=index;j<memInd[index];j++)
				builder.append(readMem(j, mem, memInd));
		final String str=builder.toString();
		if(!pat4.matcher(str).matches()){
			final short val=Short.parseShort(str);
			if(memInd[index]>val)
				memInd[index]=val;
		}else{
			System.out.println(
				new StringBuilder("\nMemory Index Expected Instead Of: ")
									.append(str)
									.toString()
				);
			return false;
		}
		return true;
	}
	private static String readMem(final short ind, final char[] mem, final short[] memInd){
		final StringBuilder builder=new StringBuilder();
		for(short i=ind;i<memInd[ind];i++)
			builder.append(mem[i]);
		return builder.toString();
	}
}
