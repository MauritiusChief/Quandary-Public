
mutable int doLeftInc(Ref counters, int myThread) {    
    return setLeft(counters, 3);
}
mutable int doRightInc(Ref counters, int myThread) {    
    return setRight(counters, 4);
}

mutable Q main(int arg) {
    mutable Ref counters = 1 . 2;
    int dummy = [ doLeftInc(counters, 0) + doRightInc(counters, 1) ];
    return dummy;
}