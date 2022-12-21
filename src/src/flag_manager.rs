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

pub struct FlagManager {
    pub compile_flag: bool,
    pub version_flag: bool,
    pub license_flag: bool,
    pub perfmes_flag: bool,
    pub nanosec_flag: bool,
    pub commmes_flag: bool,
    pub file_name: String,
}

impl FlagManager {
    pub fn new(args: &Vec<String>) -> FlagManager {
        let nanosec_flag: bool = Self::check_flag(args, "n".to_string());
        let commmes_flag: bool = Self::check_flag(args, "m".to_string());
        return FlagManager {
            compile_flag: Self::check_flag(args, "c".to_string()),
            version_flag: Self::check_flag(args, "v".to_string()),
            license_flag: Self::check_flag(args, "l".to_string()),
            perfmes_flag: nanosec_flag || commmes_flag || Self::check_flag(args, "p".to_string()),
            nanosec_flag: nanosec_flag,
            commmes_flag: commmes_flag,
            file_name: Self::get_file_name(args),
        };
    }
    fn check_flag(args: &Vec<String>, flag: String) -> bool {
        for x in args {
            if x.starts_with("-") && x.contains(&flag) {
                return true;
            }
        }
        return false;
    }
    fn get_file_name(args: &Vec<String>) -> String {
        for x in args {
            if x.ends_with(".ufb") || x.ends_with(".ufbb") {
                return x.to_string();
            }
        }
        return String::new();
    }
}
