int main(int n) {
    print(n);

    Ref list1 = (3 . (4 . nil));
    /* Ref list2 = (5 . (6 . nil)); */
    Ref list2 = ((56 . (5 . nil)) . nil) . (26 . (2 . ((8 . nil). nil)));
    
    Ref appendedList = append(list1, list2);
    print(appendedList);
    
    Ref reversedList = reverse(appendedList);
    print(reversedList);
    
    int isListResult = isList(list1);
    print(isListResult);

    Ref list3 = (3 . (5 . (5 . nil))) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) . ((2 . (3 . (56 . (92 . nil))) . nil))));
    Ref list4 = (3 . (5 . nil)) . ((2 . (8 . nil)) . ((6 . (7 . (4 . nil))) . ((2 . (3 . (56 . (92 . nil))) . nil))));
    
    int isSortedResult1 = isSorted(list3);
    print(isSortedResult1);
    int isSortedResult2 = isSorted(list4);
    print(isSortedResult2);

    return 0;
}

int isList(Ref tree) {
    if (isNil(tree) == 1) return 1;
    if (isAtom(tree) == 1) return 0;
    return isList((Ref) right(tree));
}

Ref append(Ref list1, Ref list2) {
    if (isNil(list1) == 1) return list2;
    return (Ref) (left(list1) . append((Ref) right(list1), list2));
}

Ref reverse(Ref list) {
    /* print(list); */
    if (isNil(list) == 1) return nil;
    return append(reverse((Ref) right(list)), (left(list) . nil));
}

int length(Ref list) {
    if (isNil(list) == 1) return 0;
    return 1 + length((Ref) right(list));
}
int isSorted(Ref list) {
    if (isNil(list) == 1 || isNil(right(list)) == 1) return 1;
    if ( length((Ref) left(list)) <= length((Ref) left((Ref) right(list))) ) return isSorted((Ref) right(list));
    return 0;
}

/* Very importent, because it is used to determin base case; Very importent, because there is no loop so recursion is the only way. */