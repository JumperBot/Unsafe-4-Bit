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
mod command;
mod flag_manager;
mod memory_map;
mod runner;
mod ufbc;
mod universal;

use flag_manager::FlagManager;
use runner::Runner;
use ufbc::UFBC;
use universal::Universal;

use std::env;

use reqwest::blocking::Client;
use reqwest::Method;

fn main() {
    // TODO: Always Change Version Tag Here And At Cargo.toml
    let version: &str = "v1.6.4";
    let flags: FlagManager = FlagManager::new(&env::args().collect::<Vec<String>>());
    if flags.version_flag {
        println!("Checking For The Latest Released Version...");
        if let Ok(x) = Client::new()
            .request(
                Method::GET,
                "https://api.github.com/repos/JumperBot/Unsafe-4-Bit/releases/latest",
            )
            .header("User-Agent", "Unsafe-4-Bit")
            .send()
        {
            let body: String = x.text().unwrap();
            if body.contains("tag_name") {
                let s1: &str = &body[body.find("tag_name").unwrap() + 11..];
                let s2: &str = &s1[..s1.find("\"").unwrap()];
                if !s2.eq(version) {
                    println!(
                        "UFB Is Not Up-To-Date...\n{s2} > {version} ...\nVisit The Repository And Update UFB..."
                    );
                } else {
                    println!("UFB Is Up-To-Date...");
                }
            } else {
                println!("Could Not Connect To Github...");
            }
        } else {
            println!("Could Not Connect To Github...");
        }
        println!("UFB Version: {version}\nFlag Triggered, Continuing Anyway...\n\n");
    }
    if flags.license_flag {
        println!(
            "----------------------------------------------------------------------------------
Unsafe Four Bit is a compiled-interpreted, dynamically-typed programming language.
Copyright (C) 2022  JumperBot_

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
----------------------------------------------------------------------------------
Flag Triggered, Continuing Anyway...\n\n"
        );
    }
    if flags.file_name.is_empty() {
        Universal::err_exit("There Was No File Provided.\nTerminating...".to_string());
    }
    if flags.compile_flag {
        if flags.file_name.ends_with(".ufbb") {
            Universal::err_exit(
                concat!(
                    "Could Not Compile Source Code That Had Already Been Compiled.\n",
                    "Remove The Compilation Flag To Run The Compiled Program.\n",
                    "Terminating..."
                )
                .to_string(),
            );
        }
        let compiler: UFBC = UFBC {
            file_name: flags.file_name,
        };
        compiler.compile();
    } else {
        if flags.file_name.ends_with(".ufb") {
            Universal::err_exit(
                concat!(
                    "Could Not Run Uncompiled Source Code.\n",
                    "Add The Compilation Flag To Compiled The Program.\n",
                    "Terminating..."
                )
                .to_string(),
            );
        }
        let mut runner: Runner = Runner::new(
            flags.file_name,
            flags.perfmes_flag,
            flags.nanosec_flag,
            flags.commmes_flag,
        );
        runner.run();
    }
}
