import static java.lang.Math.max;

public class SymbolicNode {
    private SymbolicNode left;
    private SymbolicNode right;
    //000: +
    //001: - (binary)
    //010: - (unary)
    //011: *
    //100: / (binary)
    //101: 1/x (unary)
    //110: * (squared)
    private final boolean[] function;
    /*
    0000    age;
    0001    menopause;
    0010    tumor_size;
    0011    inv_nodes;
    0100    node_caps;
    0101    deg_malig;
    0110    breast;
    0111    breast_quad;
    1000    irradiat;
     */
    private final boolean[] value;
    final boolean isTerminal;

    SymbolicNode(boolean[] function, boolean[] value, boolean isTerminal, SymbolicNode left, SymbolicNode right) {
        this.function = function;
        this.value = value;
        this.isTerminal = isTerminal;
        this.left = left;
        this.right = right;
    }

    void setLeft(SymbolicNode left) {
        this.left = left;
    }

    void setRight(SymbolicNode right) {
        this.right = right;
    }

    double resolve(boob data) {
        if (isTerminal) {
            if (!function[0] && !function[1] && !function[2] && !function[3]) { // 0000
                return data.age;
            } else if (!function[0] && !function[1] && !function[2] && function[3]) { // 0001
                return data.menopause;
            } else if (!function[0] && !function[1] && function[2] && !function[3]) { // 0010
                return data.tumor_size;
            } else if (!function[0] && !function[1] && function[2] && function[3]) { // 0011
                return data.inv_nodes;
            } else if (!function[0] && function[1] && !function[2] && !function[3]) { // 0100
                return data.node_caps;
            } else if (!function[0] && function[1] && !function[2] && function[3]) { // 0101
                return data.deg_malig;
            } else if (!function[0] && function[1] && function[2] && !function[3]) { // 0110
                return data.breast;
            } else if (!function[0] && function[1] && function[2] && function[3]) { // 0111
                return data.breast_quad;
            } else { // 1000
                return data.irradiat;
            }
        }


        if (!function[0] && !function[1] && !function[2]) {//+ 000
            return left.resolve(data) + right.resolve(data);
        } else if (!function[0] && !function[1] && function[2]) {//- 001
            return left.resolve(data) - right.resolve(data);
        } else if (!function[0] && function[1] && !function[2]) {//- unary 010
            return -left.resolve(data);
        } else if (!function[0] && function[1] && function[2]) {//* 011
            return left.resolve(data) * right.resolve(data);
        } else if (function[0] && !function[1] && !function[2]) {// / 100
            return left.resolve(data) / right.resolve(data);
        } else if (function[0] && !function[1] && function[2]) {// / unary 101
            return 1 / left.resolve(data);
        } else if (function[0] && function[1] && !function[2]) {// * squared unary 110
            double temp = left.resolve(data);
            return temp * temp;
        } else {
            throw new IllegalStateException("Illegal function encoding");
        }
    }

    public String toString() {
        if (isTerminal) {
            return "x";
        }

        if (!function[0] && !function[1] && !function[2]) {//+ 000
            return "("+left.toString() + "+" + right.toString()+")";
        } else if (!function[0] && !function[1] && function[2]) {//- 001
            return "("+left.toString() + "-" + right.toString()+")";
        } else if (!function[0] && function[1] && !function[2]) {//- unary 010
            return "("+"-" + left.toString()+")";
        } else if (!function[0] && function[1] && function[2]) {//* 011
            return "("+left.toString() + "*" + right.toString()+")";
        } else if (function[0] && !function[1] && !function[2]) {// / 100
            return "("+left.toString() + "/" + right.toString()+")";
        } else if (function[0] && !function[1] && function[2]) {// / unary 101
            return "(1/" + left.toString()+")";
        } else if (function[0] && function[1] && !function[2]) {// * squared unary 110
            return "("+left.toString()+")^2 ";
        } else {
            throw new IllegalStateException("Illegal function encoding");
        }
    }

    int size(){
        if(left==null && right==null) return 1;
        else if(left == null) return 1+right.size();
        else if(right==null) return 1+left.size();

        return 1+left.size()+right.size();
    }

    SymbolicNode cloneSubTree(){
        SymbolicNode root = new SymbolicNode(function, value, isTerminal, null, null);
        if(left!=null) root.setLeft(root.left.cloneSubTree());
        if(right!=null) root.setRight(root.right.cloneSubTree());
        return root;
    }

    SymbolicNode get(Integer i){
        if(i==0){
            return this;
        }
        i--;
        if(left!=null){
            SymbolicNode left = this.left.get(i);
            if(i==0) return left;
            return right.get(i);
        }
        if(right!=null){
            return right.get(i);
        }
        return null;
    }

    void set(Integer i, SymbolicNode newSubtree){
        if(i==1){
            this.left=newSubtree;
        }
        i--;
        if(left!=null){
            this.left.set(i, newSubtree);
            if(i==1) this.right=newSubtree;
            else this.right.set(i, newSubtree);
        }
        if(this.right!=null){
            this.right.set(i, newSubtree);
        }
    }

    int getDepth(){
        if(left==null && right==null) return 1;
        else if(left == null) return 1+right.getDepth();
        else if(right==null) return 1+left.getDepth();

        return 1+max(left.getDepth(),right.getDepth());
    }
}
