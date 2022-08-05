import java.math.BigInteger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.ArrayList;

class Interpreter{
	final static char[] mem=new char[256];
	final static int[] memInd=new int[256];
	public static void main(final String[]a)throws Exception{
		final byte[] data=Files.readAllBytes(Paths.get("Output.ufbb"));
		final int[] data2=new int[data.length];
		final StringBuilder builder=new StringBuilder();
		for(int i=0;i<data.length;i++)
			data2[i]=data[i]&0xff;
		for(final int i:data2)
			builder.append(manPadding(Integer.toBinaryString(i), 8));
		final String bin=builder.toString();
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
		for(int i=0;i<256;i++)
			memInd[i]=0;
		mem[0]=' ';
		for(int i=1;i<27;i++)
			mem[i]=(char)('A'+(i-1));
		for(int i=0;i<10;i++)
			mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
		final ArrayList<Integer> lines=new ArrayList<>();
		for(int i=0;i<bin.length();i+=4){
			try{
				if(!lines.contains(i))
					lines.add(i);
				System.out.println(commands[Short.parseShort(bin.substring(i, i+4), 2)]);
				switch(commands[Short.parseShort(bin.substring(i, i+4), 2)]){
					case "wvar":
						i=wvar(bin, i+4);
						break;
					case "nvar":
						i=nvar(bin, i+4);
						break;
					case "trim":
						i=trim(bin, i+4);
						break;
					case "add":
						i=add(bin, i+4);
						break;
					case "sub":
						i=sub(bin, i+4);
						break;
					case "mul":
						i=mul(bin, i+4);
						break;
					case "div":
						i=div(bin, i+4);
						break;
					case "mod":
						i=mod(bin, i+4);
						break;
					case "rmod":
						i=rmod(bin, i+4);
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
						i=print(bin, i+4);
						break;
					case "read":
						i=read(bin, i+4);
						break;
					default:
						break;
				}
			}catch(final Exception e){
				throw e;
				//System.out.println(e.toString());
			}
		}
		System.out.println(Arrays.toString(mem));
		System.out.println(Arrays.toString(memInd));
	}
	private static String rvar(final int ind){
		if(memInd[ind]==0)return Character.toString(mem[ind]);
		final StringBuilder builder=new StringBuilder();
		for(int i=ind;i<memInd[ind]+1;i++)builder.append(mem[i]);
		return builder.toString();
	}
	private static int wvar(final String str, final int offset){
		final int argCount=Short.parseShort(str.substring(offset, offset+8), 2);
		if(argCount<1)return offset+4;
		final int memIndex=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final char[] temp=rvar(memIndex).toCharArray();
		int curInd=memIndex;
		nvar(str, offset+8);
		for(int i=0;i<argCount-1;i++){
			final int ind=Short.parseShort(str.substring(offset+16+(i*8), offset+24+(i*8)), 2);
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
		return offset+(argCount*8)+4;
	}
	private static int nvar(final String str, final int offset){
		final int ind=Short.parseShort(str.substring(offset, offset+8), 2);
		if(memInd[ind]==0){
			mem[ind]='\u0000';
			return offset+4;
		}
		for(int i=ind;i<memInd[ind]+1;i++)
			if(memInd[i]==0||i==ind)
				mem[i]='\u0000';
		memInd[ind]=0;
		return offset+4;
	}
	private static int trim(final String str, final int offset){
		final int ind=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int max=Short.parseShort(str.substring(offset+8, offset+16), 2);
		if(max==0)return nvar(str, offset)+8;
		if(max>memInd[ind]-ind)return offset+12;
		final char[] temp=rvar(ind).toCharArray();
		nvar(str, offset);
		for(int i=0;i<max;i++)mem[i+ind]=temp[i];
		memInd[ind]=ind+max-1;
		return offset+12;
	}
	private static int add(final String str, final int offset){
		final int ind1=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int ind2=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(str, offset);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return offset+12;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return offset+12;
		}
		try{
			final long num1=Long.parseLong(str1);
			final long num2=Long.parseLong(str2);
			final char[] out=String.valueOf(num1+num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			final long num1=str1.hashCode();
			final long num2=str2.hashCode();
			final char[] out=String.valueOf(num1+num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}
		return offset+12;
	}
	private static int sub(final String str, final int offset){
		final int ind1=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int ind2=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(str, offset);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return offset+12;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return offset+12;
		}
		try{
			final long num1=Long.parseLong(str1);
			final long num2=Long.parseLong(str2);
			final char[] out=String.valueOf(num1-num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			final long num1=str1.hashCode();
			final long num2=str2.hashCode();
			final char[] out=String.valueOf(num1-num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}
		return offset+12;
	}
	private static int mul(final String str, final int offset){
		final int ind1=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int ind2=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(str, offset);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return offset+12;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return offset+12;
		}
		try{
			final long num1=Long.parseLong(str1);
			final long num2=Long.parseLong(str2);
			final char[] out=String.valueOf(num1*num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			final long num1=str1.hashCode();
			final long num2=str2.hashCode();
			final char[] out=String.valueOf(num1*num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}
		return offset+12;
	}
	private static int div(final String str, final int offset){
		final int ind1=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int ind2=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(str, offset);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return offset+12;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return offset+12;
		}
		try{
			final long num1=Long.parseLong(str1);
			final long num2=Long.parseLong(str2);
			final char[] out=String.valueOf(num1/num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			final long num1=str1.hashCode();
			final long num2=str2.hashCode();
			final char[] out=String.valueOf(num1/num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}
		return offset+12;
	}
	private static int mod(final String str, final int offset){
		final int ind1=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int ind2=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(str, offset);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return offset+12;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return offset+12;
		}
		try{
			final long num1=Long.parseLong(str1);
			final long num2=Long.parseLong(str2);
			final char[] out=String.valueOf(num1%num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			final long num1=str1.hashCode();
			final long num2=str2.hashCode();
			final char[] out=String.valueOf(num1%num2).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}
		return offset+12;
	}
	private static int rmod(final String str, final int offset){
		final int ind1=Short.parseShort(str.substring(offset  , offset+8 ), 2);
		final int ind2=Short.parseShort(str.substring(offset+8, offset+16), 2);
		final String str1=rvar(ind1);
		final String str2=rvar(ind2);		
		nvar(str, offset);
		if(str1.length()<1&&str2.length()>0){
			for(int i=0;i<str2.length();i++)mem[i+ind1]=str2.charAt(i);
			memInd[ind1]=ind1+str2.length()-1;
			return offset+12;
		}
		if(str2.length()<1){
			for(int i=0;i<str1.length();i++)mem[i+ind1]=str1.charAt(i);
			memInd[ind1]=ind1+str1.length()-1;
			return offset+12;
		}
		try{
			final long num1=Long.parseLong(str1);
			final long num2=Long.parseLong(str2);
			final char[] out=String.valueOf((int)(num1/num2)).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			final long num1=str1.hashCode();
			final long num2=str2.hashCode();
			final char[] out=String.valueOf((int)(num1/num2)).toCharArray();
			for(int i=ind1;i<out.length;i++)mem[ind1+i]=out[i-ind1];
			memInd[ind1]=ind1+out.length-1;
		}
		return offset+12;
	}
	private static int print(final String str, final int offset){
		final int argCount=Short.parseShort(str.substring(offset, offset+8 ), 2);
		final StringBuilder builder=new StringBuilder();
		for(int i=0;i<argCount;i++)
			builder.append(
				rvar(Short.parseShort(str.substring(offset+8+(i*8), offset+16+(i*8)), 2))
			);
		System.out.print(builder.toString());
		return offset+(argCount*8)+4;
	}
	private static int read(final String str, final int offset){
		try{
			final int ind=Short.parseShort(str.substring(offset, offset+8), 2);
			final BufferedReader scan=new BufferedReader(new InputStreamReader(System.in));
			System.out.print(">");
			final char[] in=scan.readLine().toCharArray();
			nvar(str, offset);
			for(int i=ind;i<in.length+ind;i++)mem[ind+i]=in[i-ind];
			memInd[ind]=ind+in.length-1;
		}catch(final Exception e){}
		return offset+4;
	}
	private static String manPadding(final String str, final int i){
    final StringBuilder reverse=new StringBuilder(str).reverse();
    for(;reverse.length()<i;reverse.append(0)){}
    for(;reverse.length()>i;reverse.delete(0, 1)){}
    return reverse.reverse().toString();
  }
}
