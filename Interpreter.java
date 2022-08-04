import java.math.BigInteger;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;

class Interpreter{
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
		final char[] mem=new char[256];
		final int[] memInd=new int[256];
		for(int i=0;i<256;i++)
			memInd[i]=0;
		mem[0]=' ';
		for(int i=1;i<27;i++)
			mem[i]=(char)('A'+(i-1));
		for(int i=0;i<10;i++)
			mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
		for(int i=0;i<bin.length();i+=4){
			try{
				switch(commands[Byte.parseByte(bin.substring(i, i+4), 2)]){
					case "wvar":
						i=wvar(bin, i+4, mem, memInd);
						break;
					case "nvar":
						break;
					case "trim":
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
					case "rmod":
						break;
					case "nop":
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
						i=print(bin, i+4, mem, memInd);
						break;
					case "read":
						break;
				}
			}catch(final Exception e){
				System.out.println(e.toString());
			}
		}
		System.out.println(Arrays.toString(mem));
		System.out.println(Arrays.toString(memInd));
	}
	private static String rvar(final int ind, final char[] mem, final int[] memInd){
		if(memInd[ind]==0)
			return Character.toString(mem[ind]);
		final StringBuilder builder=new StringBuilder();
		for(int i=ind;i<memInd[ind]+1;i++)
			builder.append(mem[i]);
		return builder.toString();
	}
	private static int wvar(final String str, final int offset, final char[] mem, final int[] memInd){
		final int argCount=Byte.parseByte(str.substring(offset  , offset+8 ), 2)-1;
		final int memIndex=Byte.parseByte(str.substring(offset+8, offset+16), 2);
		int curInd=memIndex;
		for(int i=0;i<argCount;i++)
			for(final char c:rvar(Byte.parseByte(str.substring(offset+16+(i*8), offset+24+(i*8)), 2), mem, memInd).toCharArray()){
				mem[curInd]=c;
				curInd++;
			}
		if(argCount==0)
			return offset+8-4;
		memInd[memIndex]=curInd-1;
		return offset+(argCount*8)-4;
	}
	private static int print(final String str, final int offset, final char[] mem, final int[] memInd){
		final int argCount=Byte.parseByte(str.substring(offset, offset+8 ), 2)-1;
		final StringBuilder builder=new StringBuilder();
		System.out.println(argCount);
		for(int i=0;i<argCount;i++)
			builder.append(rvar(Byte.parseByte(str.substring(offset+8+(i*8), offset+16+(i*8)), 2), mem, memInd));
		System.out.print(builder.toString());
		return offset+(argCount*8)-4;
	}
	private static String manPadding(final String str, final int i){
    final StringBuilder reverse=new StringBuilder(str).reverse();
    while(reverse.length()<i)
      reverse.append("0");
    while(reverse.length()>i)
      reverse.delete(0, 1);
    return reverse.reverse().toString();
  }
}
