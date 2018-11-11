public enum Task {
    A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7);

    int id;
    Task(int i) {
        id = i;
    }
    
    public static Task getTask(int i) {
        return values()[i];
    }
}
