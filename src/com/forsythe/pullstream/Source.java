package com.forsythe.pullstream;

public interface Source {
    boolean hasNext();

    int getNext();
}
