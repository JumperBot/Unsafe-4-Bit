mod universal;
mod flag_manager;

use std::env;

use flag_manager::FlagManager;

fn main(){
    let flag_manager: FlagManager=FlagManager{
        args: env::args().collect(),
    };
}

fn run(){
    let mut mem_ind: [u8; 256]=[0; 256];
    let mut mem: [char; 256]=['\u{0000}'; 256];
    init_mem(&mut mem);
    println!("Memory:");
    println!("{}", universal::arr_to_string(&mem));
    println!("Memory Index Bounds:");
    println!("{}", universal::arr_to_string(&mem_ind));
}

fn init_mem(mem: &mut [char]){
    mem[0]=' ';
    let mut i: usize=0;
    while i != 26{
        mem[i+1]=universal::convert_u32_to_char(('A' as u32)+i as u32);
        i+=1;
    }
    i=0;
    while i != 10{
        mem[i+27]=universal::convert_u32_to_char(('0' as u32)+i as u32);
        i+=1;
    }
    mem[37]='\n';
}
