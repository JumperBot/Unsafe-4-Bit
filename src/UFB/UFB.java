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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.ArrayList;

class UFB{
	public static void main(final String[]a)throws Exception{
		new Runner(a);
	}
}
class Runner{
	//----------------------------------------------------------------------//
	/**TODO: ALWAYS CHANGE VERSION TAG.
	 * DO NOT Change '1' in "1.*.*".
	 * MINOR CHANGES should go in "1.MINOR.*".
	 * PATCH CHANGES should go in "1.*.PATCH".
	 * MINOR CHANGES should give new commands/major features.
	 * PATCH CHANGES should give new flags/performance boosts/bug fixes/etc.
	**/
	final String version_tag="v1.0.0";
	//----------------------------------------------------------------------//
	final char[] mem=new char[256];
	final int[] memInd=new int[256];
	final BufferedInputStream buffer;
	final int size;
	final ArrayList<Integer> lines=new ArrayList<>();
	public Runner(final String[] args)throws Exception{
		mem[0]=' ';
		for(int i=0;i<26;i++)mem[i+1]=(char)(i+65);
		for(int i=0;i<10;i++)mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
		boolean performance=false;
		boolean nanoseconds=false; // Java doesn't mess with the CPU/Scheduler/Timer/...
		for(final String s:args){
			final String str=s.trim();
			if(str.endsWith(".ufbb")){
				final File f=new File(s);
				buffer=new BufferedInputStream(new FileInputStream(f));
				buffer.mark(Integer.MAX_VALUE);
				size=(int)f.length();
				try{
					if(performance){
						final long start=(!nanoseconds)?System.currentTimeMillis():System.nanoTime();
						run();
						System.out.println(
							String.format(
								"Program Took %d%s To Run.",
								((!nanoseconds)?System.currentTimeMillis():System.nanoTime())-start,
								(!nanoseconds)?"ms":"ns"
							)
						);
					}else
						run();
					buffer.close();
				}catch(final Exception e){
					buffer.close();
					throw new RuntimeException(e);
				}
				return;
			}else if(str.startsWith("-")){
				if(str.contains("p"))performance=true;
				if(str.contains("n"))nanoseconds=true;
				if(str.contains("v")){
					System.out.println(version_tag);
					buffer=null;
					size=0;
					return;
				}
				if(str.contains("h")){
					System.out.println(
						String.format(
							"Go To These Links:\n%s\n%s",
							"https://github.com/JumperBot/Unsafe-4-Bit/tree/master/src#ufb",
							"https://github.com/JumperBot/Unsafe-4-Bit/tree/master/test#commands"
						)
					);
					buffer=null;
					size=0;
					return;
				}
			}
		}
		buffer=null;
		size=0;
	}
	public void run()throws Exception{
		for(;byteInd<size;){
			if(!lines.contains(byteInd))lines.add(byteInd);
			final int com=next(8);
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
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					math(com-3);
					break;
				case 9:
					Thread.sleep(10);
					break;
				case 10:
				case 11:
				case 12:
				case 13:
					jump(com-10);
					break;
				case 14:
					print();
					break;
				case 15:
					read();
					break;
				default:
					System.out.println(
						String.format("\nCommand Index: %d Is Not Recognized By The Interpreter...", com)
					);
					break;
			}
		}
		for(int i=0;i<64;i++){
			if(memInd[i]!=0)System.out.println(String.format("Memory Leak At Index: %d", i));
			final int plus=i+64;
			if(memInd[plus]!=0)System.out.println(String.format("Memory Leak At Index: %d", plus));
			final int plus2=i+128;
			if(memInd[plus2]!=0)System.out.println(String.format("Memory Leak At Index: %d", plus2));
			final int plus3=i+192;
			if(memInd[plus3]!=0)System.out.println(String.format("Memory Leak At Index: %d", plus3));
		}
	}
	int byteInd=0;
	final byte[] byteArr=new byte[1];
	private int next(final int len){
		try{
			if(len==8){
				byteInd++;
				for(long skipped=buffer.skip(byteInd-1);skipped<byteInd-1;skipped+=buffer.skip(1));
				buffer.read(byteArr, 0, 1);
				buffer.reset();
				return byteArr[0]&0xff;
			}
			return (next(8)<<8)|next(8);
		}catch(final Exception e){
			throw new RuntimeException(e);
		}
	}
	private char[] rvar(final int ind){
		if(memInd[ind]==0||memInd[ind]==ind)return new char[]{mem[ind]};
		final char[] temp=new char[memInd[ind]-ind+1];
		System.arraycopy(mem, ind, temp, 0, temp.length);
		return temp;
	}
	private void wvar(){
		final int argCount=next(8);
		final int memIndex=next(8);
		final char[] temp=rvar(memIndex);
		int curInd=memIndex;
		nvar(memIndex);
		for(int i=0;i<argCount-1;i++){
			final int ind=next(8);
			if(memIndex==ind){
				if(curInd+temp.length-1>255){
					System.arraycopy(temp, 0, mem, curInd, 255-curInd+1);
					memInd[ind]=255;
					return;
				}
				System.arraycopy(temp, 0, mem, curInd, temp.length);
				curInd+=temp.length;
			}else{
				final char[] tempty=rvar(ind);
				if(curInd+tempty.length-1>255){
					System.arraycopy(tempty, 0, mem, curInd, 255-curInd+1);
					memInd[ind]=255;
					return;
				}
				System.arraycopy(tempty, 0, mem, curInd, tempty.length);
				curInd+=tempty.length;
			}
		}
		memInd[memIndex]=curInd-1;
	}
	private void nvar(final int ind){
		if(memInd[ind]==0)
			mem[ind]='\u0000';
		else
			for(int i=ind;i<memInd[ind]+1;i++)
				if(memInd[i]==0||i==ind)
					mem[i]='\u0000';
		memInd[ind]=0;
	}
	private void trim(){
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

	private long toNum(final String in){
		final char[] arr=in.toCharArray();
		// BeCoz Long#parseLong() is slow and try-catch is expensive.
		long result=0;
		for(final char c:arr){
			final int num=c-48;
			if(num<0||num>9)return in.hashCode();
			result+=num;
			result*=10;
		}
		return result/10;
	}
	private void math(final int op){
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
				return;
			}
			System.arraycopy(str2, 0, mem, ind1, str2.length);
			memInd[ind1]=ind1+str2.length-1;
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
				return;
			}
			System.arraycopy(out, 0, mem, ind1, out.length);
			memInd[ind1]=ind1+out.length-1;
		}catch(final Exception e){
			mem[ind1]='i';
			memInd[ind1]=ind1;
		}
	}
	private void jump(final int op){
		final String arg1=new String(rvar(next(8)));
		final String arg2=new String(rvar(next(8)));
		final int com=next(16);
		if(
			(op==0&&toNum(arg1)>toNum(arg2))||
			(op==1&&toNum(arg1)<toNum(arg2))||
			(op==2&&arg1.equals(arg2))||
			(op==3&&!arg1.equals(arg2))
		){
			if(com<lines.size()){
				byteInd=lines.get(com);
				return;
			}
			skip(com);
		}
	}
	private void skip(final int ind){
		if(ind>size){
			byteInd=size;
			return;
		}
		for(;lines.size()<ind&&byteInd<size;){
			lines.add(byteInd);
			final int curByte=next(8);
			if(curByte>1){
				if(curByte<9)byteInd+=2;
				else if(curByte>9){
					if(curByte<14)byteInd+=4;
					else if(curByte==15)byteInd++;
					else byteInd+=next(8)+1;
				}
			}else if(curByte==1)byteInd++;
			else byteInd+=next(8)+1;
		}
	}
	final PrintWriter out=new PrintWriter(new BufferedWriter(
		new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "UTF-8"), 512
	));
	private void print(){
		final int argCount=next(8);
		for(int i=0;i<argCount;i++)out.write(rvar(next(8)));
		out.flush();
	}
	final BufferedReader scan=new BufferedReader(new InputStreamReader(System.in));
	private void read()throws Exception{
		final int ind=next(8);
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
