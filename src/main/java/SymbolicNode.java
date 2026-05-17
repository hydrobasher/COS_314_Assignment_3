import java.util.Arrays;
import java.util.Random;

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

    double resolve(breastData data) {
        if (isTerminal) {
            if (!value[0] && !value[1] && !value[2] && !value[3]) { // 0000
                return data.age;
            } else if (!value[0] && !value[1] && !value[2] && value[3]) { // 0001
                return data.menopause;
            } else if (!value[0] && !value[1] && value[2] && !value[3]) { // 0010
                return data.tumor_size;
            } else if (!value[0] && !value[1] && value[2] && value[3]) { // 0011
                return data.inv_nodes;
            } else if (!value[0] && value[1] && !value[2] && !value[3]) { // 0100
                return data.node_caps;
            } else if (!value[0] && value[1] && !value[2] && value[3]) { // 0101
                return data.deg_malig;
            } else if (!value[0] && value[1] && value[2] && !value[3]) { // 0110
                return data.breast;
            } else if (!value[0] && value[1] && value[2] && value[3]) { // 0111
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
            if (!value[0] && !value[1] && !value[2] && !value[3]) return "age";
            else if (!value[0] && !value[1] && !value[2] && value[3]) return "menopause";
            else if (!value[0] && !value[1] && value[2] && !value[3]) return "tumor_size";
            else if (!value[0] && !value[1] && value[2] && value[3]) return "inv_nodes";
            else if (!value[0] && value[1] && !value[2] && !value[3]) return "node_caps";
            else if (!value[0] && value[1] && !value[2] && value[3]) return "deg_malig";
            else if (!value[0] && value[1] && value[2] && !value[3]) return "breast";
            else if (!value[0] && value[1] && value[2] && value[3]) return "breast_quad";
            else return "irradiat";
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
        boolean[] clonedFunction = function != null ? Arrays.copyOf(function, function.length) : null;
        boolean[] clonedValue = value != null ? Arrays.copyOf(value, value.length) : null;
        SymbolicNode root = new SymbolicNode(clonedFunction, clonedValue, isTerminal, null, null);
        if (left != null) root.setLeft(left.cloneSubTree());
        if (right != null) root.setRight(right.cloneSubTree());
        return root;
    }

    SymbolicNode get(int[] i) {
        if (i[0] == 0) return this;
        i[0]--;

        if (left != null) {
            SymbolicNode result = left.get(i);
            if (result != null) return result;
        }
        if (right != null) {
            SymbolicNode result = right.get(i);
            if (result != null) return result;
        }
        return null;
    }

    /**
     * Replaces the node at pre-order index i with newSubtree.
     * Returns the number of nodes consumed so the caller can track progress.
     */
    boolean set(int[] i, SymbolicNode newSubtree) {
        if (i[0] == 0) return false;
        i[0]--;

        if (left != null) {
            if (i[0] == 0) { left = newSubtree; return true; }
            if (left.set(i, newSubtree)) return true;
        }
        if (right != null) {
            if (i[0] == 0) { right = newSubtree; return true; }
            if (right.set(i, newSubtree)) return true;
        }
        return false;
    }

    int getDepth(){
        if(left==null && right==null) return 1;
        else if(left == null) return 1+right.getDepth();
        else if(right==null) return 1+left.getDepth();

        return 1+max(left.getDepth(),right.getDepth());
    }

    public void mutate(Random random) {
        if(isTerminal){
            SymbolicAlgorithm.populateEncodedTerminals(random.nextDouble(), this.value);
        }
        else{

            if (!function[0] && !function[1] && !function[2]) {//+ 000
                binaryFunctionMutate(random);
            } else if (!function[0] && !function[1] && function[2]) {//- 001
                binaryFunctionMutate(random);
            } else if (!function[0] && function[1] && !function[2]) {//- unary 010
                unaryFunctionMutate(random);
            } else if (!function[0] && function[1] && function[2]) {//* 011
                binaryFunctionMutate(random);
            } else if (function[0] && !function[1] && !function[2]) {// / 100
                binaryFunctionMutate(random);
            } else if (function[0] && !function[1] && function[2]) {// / unary 101
                unaryFunctionMutate(random);
            } else if (function[0] && function[1] && !function[2]) {// * squared unary 110
                unaryFunctionMutate(random);
            } else {
                throw new IllegalStateException("Illegal function encoding");
            }
        }
    }

    private void unaryFunctionMutate(Random random) {
        double chooser = random.nextDouble();
        double third=1.0/3.0;
        if (chooser < third) {
            function[0] = false;
            function[1] = true;
            function[2] = false;
        }
        else if (chooser < third * 2) {
            function[0] = true;
            function[1] = false;
            function[2] = true;
        } else {
            function[0] = true;
            function[1] = true;
            function[2] = false;
        }
    }

    private void binaryFunctionMutate(Random random) {
        double chooser = random.nextDouble();
        if (chooser < 0.25) {
            function[0] = false;
            function[1] = false;
            function[2] = false;
        } else if (chooser < 0.5) {
            function[0] = false;
            function[1] = false;
            function[2] = true;
        } else if (chooser < 0.75) {
            function[0] = false;
            function[1] = true;
            function[2] = true;
        } else {
            function[0] = true;
            function[1] = false;
            function[2] = false;
        }
    }


}