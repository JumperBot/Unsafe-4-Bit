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

class Runner{
	//----------------------------------------------------------------------//
	/**TODO: ALWAYS CHANGE VERSION TAG.
	 * DO NOT Change '1' in "1.*.*".
	 * MINOR CHANGES should go in "1.MINOR.*".
	 * PATCH CHANGES should go in "1.*.PATCH".
	 * MINOR CHANGES should give new commands/major features.
	 * PATCH CHANGES should give new flags/performance boosts/bug fixes/etc.
	**/
	final String version_tag="v1.1.3";
	//----------------------------------------------------------------------//
	final char[] mem=new char[256];
	final int[] memInd=new int[256];
	final BufferedInputStream buffer;
	final int size;
	final int[] lines;
	int furthestLine=-1;
  final FlagManager flags;
  final boolean nanoseconds;
  final boolean timeMethods;
	public Runner(final String[]a)throws Exception{
		mem[0]=' ';
		for(int i=0;i<26;i++)mem[i+1]=(char)(i+65);
		for(int i=0;i<10;i++)mem[i+27]=String.valueOf(i).charAt(0);
		mem[37]='\n';
    flags=new FlagManager(a);
    nanoseconds=flags.isFlagActivated('n');
    timeMethods=flags.isFlagActivated('m');
    final String fileName=flags.getFileName();
    final boolean performance=flags.isFlagActivated('p');
    final boolean version=flags.isFlagActivated('v');
    final boolean help=flags.isFlagActivated('h');
    if(version){
      System.out.printf(
        "UFB version: %s (master)\n%s\n\n",
        version_tag,
        "Flag triggered, continuing anyway..."
      );
    }
    if(help){
      final String repo="https://github.com/JumperBot/Unsafe-4-Bit";
      final String master="/tree/master/";
      System.out.printf(
        "%s\n%s\n%s%s%s\n%s%s%s\n%s\n%s\n%s\n\n",
        "Need help? Either visit these links:",
        repo,
        repo, master, "src#ufb",
        repo, master, "test#commands",
        "or visit the examples in the 'test' folder...",
        "only if you fully cloned the repository.",
        "Flag triggered, continuing anyway..."
      );
    }
    if(fileName.length()!=0){
      final File f=new File(fileName);
      buffer=new BufferedInputStream(new FileInputStream(f));
      buffer.mark(Integer.MAX_VALUE);
      size=(int)f.length();
      lines=new int[size];
      try{
        if(performance){
          final long start=(!nanoseconds)?
            System.currentTimeMillis():System.nanoTime();
          run();
          final long end=(!nanoseconds)?
            System.currentTimeMillis():System.nanoTime();
          System.out.println(
            String.format(
              "Program Took %d%s To Run.",
              end-start, (!nanoseconds)?"ms":"ns"
            )
          );
        }else run();
        buffer.close();
        scan.close();
      }catch(final Exception e){
        buffer.close();
        scan.close();
        throw new RuntimeException(e);
      }
      return;
    }
		buffer=null;
		size=0;
		lines=null;
		scan.close();
    System.out.println("No file input found, exitting.");
	}
	private void run()throws Exception{
		if(timeMethods){
			for(;byteInd<size;){
				if(furthestLine>-1&&lines[furthestLine]<byteInd){
					furthestLine++;
					lines[furthestLine]=byteInd;
				}else if(furthestLine<0){
					furthestLine=0;
					lines[0]=byteInd;
				}
				final int com=next(8);
				final long start=(!nanoseconds)?System.currentTimeMillis():System.nanoTime();
				runCommand(com);
				final long end=(!nanoseconds)?System.currentTimeMillis():System.nanoTime();
				System.out.println(
					String.format(
						"\nCommand Index: %d Took %d%s To Run.",
						com, end-start, (!nanoseconds)?"ms":"ns"
					)
				);
			}
		}else{
			for(;byteInd<size;){
				if(furthestLine>-1&&lines[furthestLine]<byteInd){
					furthestLine++;
					lines[furthestLine]=byteInd;
				}else if(furthestLine<0){
					furthestLine=0;
					lines[0]=byteInd;
				}
				runCommand(next(8));
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
	private void runCommand(final int com)throws Exception{
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
				Thread.sleep(10);
				break;
			case 10: case 11: case 12: case 13:
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
	private double toNum(final char[] arr){
		final int decimalInd=findPeriod(arr);
		if(decimalInd!=-1){
			double result=0;
			for(int i=0;i<decimalInd;i++){
				final int num=arr[i]-48;
				if(num<0||num>9)return new String(arr).hashCode();
				result+=num;
				result*=10;
			}
			for(int i=decimalInd+1;i<arr.length;i++){
				final int num=arr[i]-48;
				if(num<0||num>9)return new String(arr).hashCode();
				result+=num;
				result/=10;
			}
			return result;
		}else{ // BeCoz Long#parseLong() is slow and try-catch is expensive.
			double result=0;
			for(final char c:arr){
				final int num=c-48;
				if(num<0||num>9)return new String(arr).hashCode();
				result+=num;
				result*=10;
			}
			return result/10;
		}
	}
	private void math(final int op){
		final int ind1=next(8);
		final char[] str2=rvar(next(8));
		if(str2.length==0)return; // The earlier the call, the better.
		final char[] str1=rvar(ind1);
		if(str1.length<1&&str2.length>0){
			write(0, ind1, false, str2);
			return;
		}
		final double num1=toNum(str1);
		final double num2=toNum(str2);
		try{
			final String val=String.valueOf(
				(op==0)?num1+num2:(op==1)?num1-num2:
				(op==2)?num1*num2:(op==3)?num1/num2:
				(op==4)?num1%num2:(int)	 (num1/num2)
			);
			if(val.equals("NaN")){
				nvar(ind1);
				mem[ind1]='i';
				memInd[ind1]=ind1;
				return;
			}
			final char[] out=(
				(val.endsWith(".0"))?val.substring(0, val.length()-2):val
			).toCharArray();
			write(0, ind1, false, out);
		}catch(final Exception e){
			nvar(ind1);
			mem[ind1]='i';
			memInd[ind1]=ind1;
		}
	}

	private void jump(final int op){
		final char[] arg1=rvar(next(8));
		final char[] arg2=rvar(next(8));
		final int com=next(16);
		if(
			(op==0&&toNum(arg1)>toNum(arg2))||
			(op==1&&toNum(arg1)<toNum(arg2))||
			(op==2&&new String(arg1).equals(new String(arg2)))||
			(op==3&&!new String(arg1).equals(new String(arg2)))
		){
			if(com<furthestLine+1){
				byteInd=lines[com];
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
