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

class RfileCommand extends NeedsArgLengthCommand{
  public RfileCommand(final String[] line, final String[] realLine){
    super(17, line, realLine);
  }
  @Override
  public void checkCases(final String[] line, final String[] realLine){
    super.checkCases(line, realLine);
    try{
      if(Long.parseLong(line[1])<38)
        errors.append(Universal.formatError(
          line, "Memory Index", line[1],
          "Endangers A Read-Only Memory Index"
        ));
    }catch(final Exception e){
      errors.append(Universal.formatError(
        line, "Memory Index Expected Instead Of", line[1],
        "Should Be Replaced With A Memory Index"
      ));
    }
  }
}