import java.math.BigInteger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.ArrayList;

class UFB{
	final static char[] mem=new char[256];
	final static int[] memInd=new int[256];
	static short[] bytes;
	public static void main(final String[]a)throws Exception{
		final byte[] tempBytes=Files.readAllBytes(Paths.get(a[0].trim()));
		final int size=tempBytes.length;
		bytes=new short[size];
		for(int i=0;i<size;i++)bytes[i]=(short)(tempBytes[i]&0xff);
		run();
	}
	public static void run()throws Exception{
		final String[] commands={
			"wvar" , "nvar",
			"trim" , "add",
			"sub"  , "mul",
			"div"  , "mod",
			"rmod" , "nop",
			"jm"   , "jl",
			"je"   , "jne",
			"print", "read"
		};
		for(int i=0;i<256;i++)memInd[i]=0;
		mem[0]=' ';
		for(int i=1;i<27;i++)mem[i]=(char)('A'+(i-1));
		for(int i=0;i<10;i++)mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
		final ArrayList<Integer> lines=new ArrayList<>();
		final int size=bytes.length;
		for(;byteInd<size;){
			try{
				if(!lines.contains(byteInd))lines.add(byteInd);
				final String com=commands[next(8)];
				//System.out.println(com);
				switch(com){
					case "wvar":
						wvar();
						break;
					case "nvar":
						nvar(next(8));
						break;
					case "trim":
						trim();
						break;
					case "add":
						math(0);
						break;
					case "sub":
						math(1);
						break;
					case "mul":
						math(2);
						break;
					case "div":
						math(3);
						break;
					case "mod":
						math(4);
						break;
					case "rmod":
						math(5);
						break;
					case "nop":
						try{
							Thread.sleep(10);
						}catch(final Exception nop){}
						break;
					case "jm":
						break;
					case "jl":
						break;
					case "je":
						break;
					case "jne":
						break;
					case "print":
						print();
						break;
					case "read":
						read();
						break;
					default:
						break;
				}
			}catch(final Exception e){
				throw e;
				//System.out.println(e.toString());
			}
		}
		//System.out.println(Arrays.toString(mem));
		//System.out.println(Arrays.toString(memInd));
	}
	static int byteInd=0;
	private static int next(final int len){
		byteInd++;
		if(len==8)return bytes[byteInd-1];
		return Integer.parseInt(
			manPadding(Integer.toBinaryString(next(8)), 8)+
			manPadding(Integer.toBinaryString(next(8)), 8)
		);
	}
	private static String rvar(final int ind){
		if(memInd[ind]==0)return Character.toString(mem[ind]);
		final StringBuilder builder=new StringBuilder();
		for(int i=ind;i<memInd[ind]+1;i++)builder.append(mem[i]);
		return builder.toString();
	}
	private static void wvar(){
		final int argCount=next(8);
		if(argCount<1)return;
		final int memIndex=next(8);
		//System.out.println(argCount+"|"+memIndex);
		final char[] temp=rvar(memIndex).toCharArray();
		int curInd=memIndex;
		nvar(memIndex);
		for(int i=0;i<argCount-1;i++){
			final int ind=next(8);
			if(memIndex==ind){
				for(final char c:temp){
					mem[curInd]=c;
					curInd++;
				}
				curInd--;
			}
			else for(final char c:rvar(ind).toCharArray())mem[curInd]=c;
			curInd++;
		}
		memInd[memIndex]=curInd-1;
	}
	private static void nvar(final int ind){
		if(memInd[ind]==0)
			mem[ind]='\u0000';
		else
			for(int i=ind;i<memInd[ind]+1;i++)
				if(memInd[i]==0||i==ind)
					mem[i]='\u0000';
		memInd[ind]=0;
	}
	private static void trim(){
		final int ind=next(8);
		final int max=next(8);
		if(max==0){
			nvar(ind);
			return;
		}
		if(max>memInd[ind]-ind)return;
		final char[] temp=rvar(ind).toCharArray();
		nvar(ind);
		for(int i=0;i<max;i++)mem[i+ind]=temp[i];
		memInd[ind]=ind+max-1;
	}
	private static void math(final int op){
		final int ind1=next(8);
		final int ind2=next(8);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(ind1);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return;
		}
		long num1;
		long num2;
		try{
			num1=Long.parseLong(str1);
		}catch(final Exception e){
			num1=str1.hashCode();
		}
		try{
			num2=Long.parseLong(str2);
		}catch(final Exception e){
			num2=str2.hashCode();
		}
		try{
			final char[] out=String.valueOf(
				(op==0)?num1+num2:(op==1)?num1-num2:
				(op==2)?num1*num2:(op==3)?num1/num2:
				(op==4)?num1%num2:(long) (num1/num2)
			).toCharArray();
			for(int i=0;i<out.length;i++)mem[ind1+i]=out[i];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){ //Divided By Zero, Mate?
			mem[ind1]='i';
			memInd[ind1]=ind1;
		}
	}
	private static void print(){
		final int argCount=next(8);
		final StringBuilder builder=new StringBuilder();
		for(int i=0;i<argCount;i++)builder.append(rvar(next(8)));
		System.out.print(builder.toString());
	}
	private static void read(){
		try{
			final int ind=next(8);
			final BufferedReader scan=new BufferedReader(new InputStreamReader(System.in));
			System.out.print(">");
			final char[] in=scan.readLine().toCharArray();
			nvar(ind);
			for(int i=ind;i<in.length+ind;i++)mem[ind+i]=in[i-ind];
			memInd[ind]=ind+in.length-1;
		}catch(final Exception e){}
	}
	private static String manPadding(final String str, final int i){
    final StringBuilder reverse=new StringBuilder(str).reverse();
    for(;reverse.length()<i;reverse.append(0)){}
    for(;reverse.length()>i;reverse.delete(0, 1)){}
    return reverse.reverse().toString();
  }
}
