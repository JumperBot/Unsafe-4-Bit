import java.math.BigInteger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;

import java.nio.MappedByteBuffer;

import java.util.Arrays;
import java.util.ArrayList;

class UFB{
	final static char[] mem=new char[256];
	final static int[] memInd=new int[256];
	static int[] bytes;
	public static void main(final String[]a)throws Exception{
		try(final FileChannel fileChannel=new RandomAccessFile(a[0], "r").getChannel()){
			final MappedByteBuffer buffer=fileChannel.map(
				FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()
			);
			final int size=(int)fileChannel.size();
			final byte[] tempBytes=new byte[size];
			buffer.get(tempBytes);
			bytes=new int[size];
			for(int i=0;i<size;i++)bytes[i]=tempBytes[i]&0xff;
		}
		mem[0]=' ';
		for(int i=0;i<26;i++)mem[i+1]=(char)(i+65);
		for(int i=0;i<10;i++)mem[i+27]=Character.forDigit(i, 10);
		mem[37]='\n';
		for(int i=38;i<256;i++)mem[i]='\u0000';
		run();
	}
	public static void run()throws Exception{
		final ArrayList<Integer> lines=new ArrayList<>();
		final int size=bytes.length;
		for(;byteInd<size;){
			try{
				if(!lines.contains(byteInd))lines.add(byteInd);
				final int com=next(8);
				//System.out.println(com);
				switch(com){
					case 0:
						wvar();
						break;
					case 1:
						nvar(next(8));
						break;
					case 2:
						trim();
						break;
					case 3:
						math(0);
						break;
					case 4:
						math(1);
						break;
					case 5:
						math(2);
						break;
					case 6:
						math(3);
						break;
					case 7:
						math(4);
						break;
					case 8:
						math(5);
						break;
					case 9:
						try{
							Thread.sleep(10);
						}catch(final Exception nop){}
						break;
					case 10: // jm, jl, je, jne
						jump(0, lines);
						break;
					case 11:
						jump(1, lines);
						break;
					case 12:
						jump(2, lines);
						break;
					case 13:
						jump(3, lines);
						break;
					case 14:
						print();
						break;
					case 15:
						read();
						break;
					default:
						break;
				}
				System.out.println(com);
				System.out.println(Arrays.toString(mem));
				System.out.println(Arrays.toString(memInd));
			}catch(final Exception e){
				throw e;
				//System.out.println(e.toString());
			}
		}
		for(int i=0;i<256;i++){
			if(memInd[i]!=0)
				System.out.println("Memory Leak At Index: "+String.valueOf(i));
		}
		System.out.println(Arrays.toString(mem));
		System.out.println(Arrays.toString(memInd));
	}
	static int byteInd=0;
	private static int next(final int len){
		if(len==8){
			byteInd++;
			return bytes[byteInd-1];
		}
		return (next(8)<<8)|next(8);
	}
	private static char[] rvar(final int ind){
		if(memInd[ind]==0)return new char[]{mem[ind]};
		final char[] temp=new char[memInd[ind]-ind+1];
		System.arraycopy(mem, ind, temp, 0, temp.length);
		return temp;
	}
	private static void wvar(){
		final int argCount=next(8);
		final int memIndex=next(8);
		final char[] temp=rvar(memIndex);
		int curInd=memIndex;
		nvar(memIndex);
		for(int i=0;i<argCount-1;i++){
			final int ind=next(8);
			if(memIndex==ind){
				if(curInd+temp.length-1>255){
					System.arraycopy(temp, 0, mem, curInd, 255-ind+1);
					memInd[ind]=255;
					return;
				}else{
					System.arraycopy(temp, 0, mem, curInd, temp.length);
					curInd+=temp.length;
				}
			}else{
				final char[] tempty=rvar(ind);
				if(ind+tempty.length-1>255){
					System.arraycopy(tempty, 0, mem, curInd, 255-ind+1);
					memInd[ind]=255;
					return;
				}else{
					System.arraycopy(tempty, 0, mem, curInd, tempty.length);
					curInd+=tempty.length;
				}
			}
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
		final char[] temp=rvar(ind);
		nvar(ind);
		System.arraycopy(temp, 0, mem, ind, max);
		memInd[ind]=ind+max-1;
	}
	private static long toNum(final String in){
		try{
			return Long.parseLong(in);
		}catch(final Exception e){
			return in.hashCode();
		}
	}
	private static void math(final int op){
		final int ind1=next(8);
		final int ind2=next(8);
		final char[] str1=rvar(ind1);
		final char[] str2=rvar(ind2);
		if(str2.length<1)return;
		nvar(ind1);
		if(str1.length<1&&str2.length>0){
			if(ind1+str2.length-1>255){
				System.arraycopy(str2, 0, mem, ind1, 255-ind1+1);
				memInd[ind1]=255;
			}else{
				System.arraycopy(str2, 0, mem, ind1, str2.length);
				memInd[ind1]=ind1+str2.length-1;
			}
			return;
		}
		final long num1=toNum(new String(str1));
		final long num2=toNum(new String(str2));
		try{
			final char[] out=String.valueOf(
				(op==0)?num1+num2:(op==1)?num1-num2:
				(op==2)?num1*num2:(op==3)?num1/num2:
				(op==4)?num1%num2:(long) (num1/num2)
			).toCharArray();
			if(ind1+out.length-1>255){
				System.arraycopy(out, 0, mem, ind1, 255-ind1+1);
				memInd[ind1]=255;
			}else{
				System.arraycopy(out, 0, mem, ind1, out.length);
				memInd[ind1]=ind1+out.length-1;
			}
		}catch(final Exception e){ //Divided By Zero, Mate?
			mem[ind1]='i';
			memInd[ind1]=ind1;
		}
	}
	private static void jump(final int op, final ArrayList<Integer> lines){
		final long arg1=toNum(new String(rvar(next(8))));
		final long arg2=toNum(new String(rvar(next(8))));
		if(
			(op==0&&arg1>arg2)||
			(op==1&&arg1<arg2)||
			(op==2&&arg1==arg2)||
			(op==3&&arg1!=arg2)
		){
			final int com=next(16);
			if(com<lines.size()+1){
				byteInd=lines.get(com);
				return;
			}
			skip(com, lines);
		}
	}
	private static void skip(final int ind, final ArrayList<Integer> lines){
		if(ind>bytes.length){
			byteInd=bytes.length;
			return;
		}
		for(;lines.size()<ind&&byteInd<bytes.length;){
			lines.add(byteInd);
			final int curByte=next(8);
			if(curByte>1){
				if(curByte<9)
					next(16);
				else if(curByte>9){
					if(curByte<14){
						next(16);
						next(16);
					}else if(curByte==15)
						next(8);
					else{
						final int temp=next(8);
						for(int i=0;i<temp;i++)
							next(8);
						// Complicated situation.
					}
				}
			}else if(curByte==1)
				next(8);
			else{
				final int temp=next(8);
				for(int i=0;i<temp;i++)
					next(8);
			}
		}
	}
	private static void print(){
		final int argCount=next(8);
		final StringBuilder builder=new StringBuilder();
		for(int i=0;i<argCount;i++)builder.append(rvar(next(8)));
		System.out.print(builder.toString());
	}
	private static void read()throws Exception{
		final int ind=next(8);
		final BufferedReader scan=new BufferedReader(new InputStreamReader(System.in));
		System.out.print("=>");
		final char[] in=scan.readLine().toCharArray();
		nvar(ind);
		if(ind+in.length-1>255){
			System.arraycopy(in, 0, mem, ind, 255-ind+1);
			memInd[ind]=255;
		}else{
			System.arraycopy(in, 0, mem, ind, in.length);
			memInd[ind]=ind+in.length-1;
		}
	}
}
