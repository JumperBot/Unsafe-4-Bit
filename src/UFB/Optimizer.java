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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import java.util.HashMap;

class Optimizer{
	final char[] mem=new char[256];
	final int[] memInd=new int[256];
  final boolean[] aKnownNonNum=new boolean[256];
	final BufferedInputStream buffer;
	final int size;
	final int[] lines;
	int furthestLine=-1;
	final StringBuilder printProxy=new StringBuilder();
	final StringBuilder newCommands=new StringBuilder();
	public Optimizer(final String file)throws Exception{
		mem[0]=' ';
    aKnownNonNum[0]=true;
		for(int i=0;i<26;i++){
      final int ind=i+1;
      aKnownNonNum[ind]=true;
      mem[ind]=(char)(ind+64);
    }
		for(int i=0;i<10;i++)mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
    for(int i=37;i<256;i++)aKnownNonNum[i]=true;
		final File f=new File(file);
		buffer=new BufferedInputStream(new FileInputStream(f));
		buffer.mark(Integer.MAX_VALUE);
		size=(int)f.length();
		lines=new int[size];
		try{
			run();
			buffer.close();
			final String newFileName=file.substring(0, file.lastIndexOf("."))+".optimized.ufb";
      final File newGenCode=new File(newFileName);
			try(final FileWriter writer=new FileWriter(newGenCode)){
				writer.write(newCommands.append("\nnvar 38").toString().trim().replaceAll("\n{2,}", "\n"));
			}
			UFBC.compile(newFileName, false);
      newGenCode.delete();
      new File(file).delete();
      final File newCompiledFile=new File(newFileName+"b");
      newCompiledFile.renameTo(new File(file));
		}catch(final Exception e){
			buffer.close();
			if(!e.toString().contains("Code cannot be optimized"))throw new RuntimeException(e);
			else System.out.println("Code cannot be optimized, but compilation is a success!");
		}
	}
	private void addToCommands(){
		final String converted=convertToMemory(
			printProxy.toString() // i == -255, - == -254, . == -253
		).replace("-255", "\nwvar 38 27\ndiv 38 27\nprint 38\nprint 255")
		 .replace("-254", "\nwvar 38 27\nsub 38 28\ntrim 38 1\nprint 38\nprint 255")
	 	 .replace("-253", "\nwvar 38 28\ndiv 38 29\nprint 39\nprint 255")
 		 .replace("\n ", "\n");
		printProxy.setLength(0);
		if(!converted.startsWith("\n"))newCommands.append("print ");
		newCommands.append(converted).append("\n");
	}
	private void run()throws Exception{
		final long start=System.currentTimeMillis();
		for(;byteInd<size;){
			if(System.currentTimeMillis()-start>5000){
				System.out.println("Optimizer: \"Timeout!\"");
				throw new Exception("Code cannot be optimized, but compilation is a success!");
			}
			if(furthestLine>-1&&lines[furthestLine]<byteInd){
				furthestLine++;
				lines[furthestLine]=byteInd;
			}else if(furthestLine<0){
				furthestLine=0;
				lines[0]=byteInd;
			}
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
				case 3: case 4: case 5: case 6: case 7: case 8:
					math(com-3);
					break;
				case 9:
					// Need to dump printProxy when called to preserve no-op.
					if(printProxy.length()!=0)addToCommands();
					newCommands.append("nop\n");
					break;
				case 10: case 11: case 12: case 13:
					if(jump(com-10))
						throw new Exception("Code cannot be optimized, but compilation is a success!");
					break;
				case 14:
					print();
					break;
				// Read not supported for optimization.
			}
		}
		if(printProxy.length()!=0)addToCommands();
	}
	final HashMap<Character, Integer> memMap=new HashMap<>(){{
		put(' ', 0);
		put('A', 1);
		put('B', 2);
		put('C', 3);
		put('D', 4);
		put('E', 5);
		put('F', 6);
		put('G', 7);
		put('H', 8);
		put('I', 9);
		put('J', 10);
		put('K', 11);
		put('L', 12);
		put('M', 13);
		put('N', 14);
		put('O', 15);
		put('P', 16);
		put('Q', 17);
		put('R', 18);
		put('S', 19);
		put('T', 20);
		put('U', 21);
		put('V', 22);
		put('W', 23);
		put('X', 24);
		put('Y', 25);
		put('Z', 26);
		put('0', 27);
		put('1', 28);
		put('2', 29);
		put('3', 30);
		put('4', 31);
		put('5', 32);
		put('6', 33);
		put('7', 34);
		put('8', 35);
		put('9', 36);
		put('\n', 37);
		put('\u0000', 38);
		//hack-characters
		put('i', -255);
		put('-', -254);
		put('.', -253);
	}};
	private String convertToMemory(final String in){
		final StringBuilder output=new StringBuilder();
		for(final char c:in.toCharArray())output.append(memMap.get(c)).append(" ");
		return output.toString().trim();
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

	final char[] emptyArr={};
	private void wvar(){
		write(next(8), next(8), true, emptyArr);
	}
	private void write(final int argCount, final int memIndex,
										 final boolean fromMem, final char[] chars){
    aKnownNonNum[memIndex]=false;
		if(fromMem){
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
			return;
		}
		nvar(memIndex);
		final int memEndPoint=memIndex+chars.length-1;
		if(memEndPoint>255){
			System.arraycopy(chars, 0, mem, memIndex, 255-memIndex+1);
			memInd[memIndex]=255;
			return;
		}
		System.arraycopy(chars, 0, mem, memIndex, chars.length);
		memInd[memIndex]=memEndPoint;
	}

	private void nvar(final int ind){
		if(memInd[ind]==0)return;
		final char[] temp=new char[memInd[ind]-ind+1]; // To Avoid For-Loops.
		System.arraycopy(temp, 0, mem, ind, temp.length);
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

	private int findPeriod(final char[] arr){
		final int half=arr.length/2;
		for(int i=0;i<half+1;i++){
			if(arr[i]=='.')return i;
			if(arr[arr.length-1-i]=='.')return arr.length-i;
		}
		return -1;
	}
	private double toNum(final char[] arr, final int ind){
    if(aKnownNonNum[ind])return hash(arr);
		final int decimalInd=findPeriod(arr);
		if(decimalInd!=-1){
			final double[] result=new double[2];
			for(int i=0;i<decimalInd;i++){
				final int num=arr[i]-48;
				if(num<0||num>9){
          aKnownNonNum[ind]=true;
          return hash(arr);
        }
				result[0]+=num;
				result[0]*=10;
        final int i2=decimalInd+1+i;
				final int num2=arr[i2]-48;
				if(num2<0||num2>9){
          aKnownNonNum[ind]=true;
          return hash(arr);
        }
				result[1]+=num2;
				result[1]/=10;
			}
			return (result[0]/10)+result[1];
		}else{ // BeCoz Long#parseLong() is slow and try-catch is expensive.
			double result=0;
			for(final char c:arr){
				final int num=c-48;
				if(num<0||num>9){
          aKnownNonNum[ind]=true;
          return hash(arr);
          // return new String(arr).hashCode();
        }
				result+=num;
				result*=10;
			}
			return result/10;
		}
	}
  // A rip-off of String#hashCode();
  // It just does too much, had to rewrite it.
  private long hash(final char[] arr){
    int hash=0;
    for(int i=0;i<arr.length;i++)
      hash=31*hash+arr[i];
    return hash;
  }
	private void math(final int op){
		final int ind1=next(8);
    final int ind2=next(8);
		final char[] str2=rvar(ind2);
		if(str2.length==0)return; // The earlier the call, the better.
		final char[] str1=rvar(ind1);
		if(str1.length<1&&str2.length>0){
			write(0, ind1, false, str2);
			return;
		}
		try{
      final double num1=toNum(str1, ind1);
      final double num2=toNum(str2, ind2);
      final double result=(op==0)?num1+num2:(op==1)?num1-num2:
                          (op==2)?num1*num2:(op==3)?num1/num2:
                          (op==4)?num1%num2:(int)	 (num1/num2);
      if(result!=result){ // Refer to Double#isNan(double v)
        nvar(ind1);
        mem[ind1]='i';
        memInd[ind1]=ind1;
        return;
      }
			if(result%1==0) write(0, ind1, false, Long.toString((long)result).toCharArray());
      else write(0, ind1, false, Double.toString(result).toCharArray());
		}catch(final Exception e){
			nvar(ind1);
			mem[ind1]='i';
			memInd[ind1]=ind1;
		}
	}

	final HashMap<Integer, Integer> jumpBackFrequency=new HashMap<Integer, Integer>();
	private boolean jump(final int op){ // Returns true if optimization should stop.
    final int ind1=next(8);
    final int ind2=next(8);
		final char[] arg1=rvar(ind1);
		final char[] arg2=rvar(ind2);
		final int com=next(16);
		if(
			(op==0&&toNum(arg1, ind1)>toNum(arg2, ind2))||
			(op==1&&toNum(arg1, ind1)<toNum(arg2, ind2))||
			(op==2&&new String(arg1).equals(new String(arg2)))||
			(op==3&&!new String(arg1).equals(new String(arg2)))
		){
			if(com<furthestLine+1){
				byteInd=lines[com];
				jumpBackFrequency.put(byteInd, jumpBackFrequency.getOrDefault(byteInd, 0)+1);
				if(jumpBackFrequency.get(byteInd)==10001)return true;
			}else skip(com);
		}
		return false;
	}
	private void skip(final int ind){
		if(ind>size){
			byteInd=size;
			return;
		}
		for(;furthestLine++<ind&&byteInd<size;){
			lines[furthestLine]=byteInd;
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
		for(int i=0;i<argCount;i++){
			printProxy.append(rvar(next(8)));
			if(printProxy.length()>100)addToCommands();
		}
	}
}
