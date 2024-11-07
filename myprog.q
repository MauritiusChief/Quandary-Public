
mutable int main(int x) {
    if (x == 1) {
        return qOne(0);
    } else if (x == 2){ 
        return qTwo(0);
    }else if (x == 3){
        return qThree(0);
    } else if (x == 4) {
        return qFour(0);
    }
    return x;
}

/*
1. Make your program so that, for an argument of 1 and using mark-sweep, it runs out of memory when using a heap size of 384 but not a heap size of 408.

More precisely, your program should give process return code 5 for

ref/quandary -gc MarkSweep -heapsize 384 myprog.q 1

but give process return code 0 for

ref/quandary -gc MarkSweep -heapsize 408 myprog.q 1
*/

int qOne(int i) {
    if (i < 17) {
        Ref x = 42 . nil;
        return qOne(i + 1);
    }
    return 42;
}

/*
2. Make your program so that, for an argument of 2 and a fixed heap size (384 bytes), it runs out of memory using reference counting but not using mark-sweep.

More precisely, your program should give process return code 5 for

ref/quandary -gc RefCount -heapsize 384 myprog.q 2

but give process return code 0 for

ref/quandary -gc MarkSweep -heapsize 384 myprog.q 2
*/

mutable int qTwo(int i) {
    if (i <17) {
        mutable Ref a = 42 . nil;
        mutable Ref b = 42 . nil;
        setRight(a, b);
        setRight(b, a);
        a = nil;
        b = nil;
        qTwo(i + 1);
    }
    return 42;
}

/*
3. Make your program so that, for an argument of 3 and a fixed heap size (384 bytes), it runs out of memory using mark-sweep but not explicit memory management.

More precisely, your program should give process return code 5 for

ref/quandary -gc MarkSweep -heapsize 384 myprog.q 3

but give process return code 0 for

ref/quandary -gc Explicit -heapsize 384 myprog.q 3
*/

int qThree(int i) {
    if (i < 17) {
        Ref x = 42 . nil;
        free(x);
        return qThree(i + 1);
    }
    return 42;
}

/*
4. Make your program so that, for an argument of 4 and a fixed heap size (384 bytes), it runs out of memory using explicit memory management but not mark-sweep GC.

More precisely, your program should give process return code 5 for

ref/quandary -gc Explicit -heapsize 384 myprog.q 4

but give process return code 0 for

ref/quandary -gc MarkSweep -heapsize 384 myprog.q 4
*/

mutable int qFour(int i) {
    if (i <17) {
        mutable Ref a = 42 . nil;
        a = nil;
        qFour(i + 1);
    }
    return 42;
}