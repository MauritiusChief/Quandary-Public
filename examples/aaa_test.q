
mutable int doLeftInc(Ref counters, int myThread) {    
    return setLeft(counters, 3);
}
mutable int doRightInc(Ref counters, int myThread) {    
    return setRight(counters, 4);
}

mutable Q main(int arg) {
    mutable Ref counters = 1 . 2;
    /* setLeft(counters, 3); */
    setRight(counters, 5);
    int dummy = right(counters);
    return dummy;
}