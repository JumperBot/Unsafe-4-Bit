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
use std::io::stdout;
use std::io::Write;
use std::time::Duration;

/*
https://stackoverflow.com/questions/23346757/make-http-request-in-rust-using-std
use std::net::TcpStream;
use std::net::ToSocketAddrs;
use std::time::Duration;
use std::io::Read;
use std::io::Write;
let mut socket = TcpStream::connect(
    &"20.205.243.168:300".to_socket_addrs().unwrap().next().unwrap()
).unwrap();
let header = format!("GET /repos/JumperBot/Unsafe-4-Bit/releases/latest HTTP/1.0\r\nHost: api.github.com\r\n");
socket.write(header.as_bytes()).unwrap();
let resp = socket.read(&mut [0; 256]).unwrap();
println!("{resp}");
*/

// "https://api.github.com/repos/JumperBot/Unsafe-4-Bit/releases/latest"
#[tokio::main]
async fn main() {
    let flags: FlagManager = FlagManager::new(&env::args().collect::<Vec<String>>());
    if flags.version_flag {
        let body=reqwest::get("https://www.rust-lang.org")
            .await?
            .text()
            .await?;
        println!("body = {:?}", body);
        // TODO: Always Change Version Tag Here And At Cargo.toml
        println!("UFB Version: v1.6.3\nFlag Triggered, Continuing Anyway...\n\n");
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

