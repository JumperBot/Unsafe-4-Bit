use std::char;

fn main(){
    let mut mem_ind: [u8; 256]=[0; 256];
    let mut mem: [char; 256]=['\u{0000}'; 256];
    init_mem(&mut mem);

    println!("Memory:");
    print_arr(&mem);
    println!("Memory Index Bounds:");
    print_arr(&mem_ind);

}

fn init_mem(mem: &mut [char]){
    mem[0]=' ';
    let mut i: usize=0;
    while i != 26{
        let c=match char::from_u32(('A' as u32)+i as u32){
            None => '\u{0000}',
            Some(c) => c,
        };
        mem[i+1]=c;
        i+=1;
    }
    i=0;
    while i != 10{
        let c=match char::from_u32(('0' as u32)+i as u32){
            None => '\u{0000}',
            Some(c) => c,
        };
        mem[i+27]=c;
        i+=1;
    }
    mem[37]='\n';
}


fn print_arr<T: std::fmt::Debug>(arr: &[T]){
    for x in arr{
        print!("{:#?}, ", x);
    }
    println!();
}

