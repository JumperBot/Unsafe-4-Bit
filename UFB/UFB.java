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

import java.math.BigInteger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import java.nio.channels.FileChannel;

import java.nio.MappedByteBuffer;

import java.util.Arrays;
import java.util.ArrayList;

class UFB{
	public static void main(final String[]a)throws Exception{
		final Runner runner=new Runner(a[0]);
	}
}
class Runner{
	final char[] mem=new char[256];
	final int[] memInd=new int[256];
	final FileChannel channel;
	final MappedByteBuffer buffer;
	final int size;
	public Runner(final String file)throws Exception{
		mem[0]=' ';
		for(int i=0;i<26;i++)mem[i+1]=(char)(i+65);
		for(int i=0;i<10;i++)mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
		channel=new RandomAccessFile(file, "r").getChannel();
		try{
			size=(int)channel.size();
			buffer=channel.map(FileChannel.MapMode.READ_ONLY, 0, size);
			run();
		}catch(final Exception e){
			channel.close();
			throw new RuntimeException(e);
		}
	}
	public void run()throws Exception{
		final ArrayList<Integer> lines=new ArrayList<>();
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
					jump(com-10, lines);
					break;
				case 14:
					print();
					break;
				case 15:
					read();
					break;
				default:
					System.out.println(String.format("\nCommandIndex: %d Is Not Recognized By The Interpreter...", com));
					break;
			}
		}
		for(int i=0;i<256;i++){
			if(memInd[i]!=0)
				System.out.println("Memory Leak At Index: "+String.valueOf(i));
		}
	}
	int byteInd=0;
	private int next(final int len){
		if(len==8){
			byteInd++;
			return ((byte)buffer.get(byteInd-1))&0xff;
		}
		return (next(8)<<8)|next(8);
	}
	private char[] rvar(final int ind){
		if(memInd[ind]==0)return new char[]{mem[ind]};
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
		try{
			return Long.parseLong(in);
		}catch(final Exception e){
			return in.hashCode();
		}
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
		}catch(final Exception e){
			mem[ind1]='i';
			memInd[ind1]=ind1;
		}
	}
	private void jump(final int op, final ArrayList<Integer> lines){
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
			skip(com, lines);
		}
	}
	private void skip(final int ind, final ArrayList<Integer> lines){
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
	private void print(){
		final int argCount=next(8);
		final StringBuilder builder=new StringBuilder();
		for(int i=0;i<argCount;i++)builder.append(rvar(next(8)));
		System.out.print(builder.toString());
	}
	private void read()throws Exception{
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
