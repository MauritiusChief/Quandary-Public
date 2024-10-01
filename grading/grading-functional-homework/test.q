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

int genericEquals(Q item1, Q item2) {
    if (isNil(item1) != isNil(item2)) {
        return 0;
    } else {
        if (isNil(item1) == 1) {
            return 1;
        }
    }
    if (isAtom(item1) != isAtom(item2)) {
        return 0;
    } else {
        if (isAtom(item1) == 1) {
            if ((int)item1 == (int)item2) { /* ??? */
                return 1;
            } else {
                return 0;
            }
        }
    }
    /* item1 and item2 are Ref's */
    if (genericEquals(left((Ref)item1), left((Ref)item2)) == 1 && genericEquals(right((Ref)item1), right((Ref)item2)) == 1) {
        return 1;
    }
    return 0;
}



int main(int arg) {
    Ref input = (nil . ((314 . nil) . ((15 . nil) . ((926 . (535 . (89 . (79 . nil)))) . ((3 . (2 . (3 . (8 . (4 . nil))))) . nil))))); /* Complicated example */
    if (isSorted(input) != 0) {
        return 1;
    }
    return 0;
}

