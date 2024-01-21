package fxplayer.focuscontrol;

import java.util.List;

public abstract class FocusSubController implements FocusTraversalComponent {
    List<FocusTraversalComponent> focusTraversalComponents;
    private int focus = -1;
    FocusTraversalComponent parentController;

    public void init(List<FocusTraversalComponent> focusTraversalComponents, FocusTraversalComponent parentController){
        this.focusTraversalComponents = focusTraversalComponents;
        this.parentController = parentController;
    }

    public boolean focusForward() {
        if (focusTraversalComponents.isEmpty())
            return true;


        if (focus > -1) {
            if (focusTraversalComponents.get(focus) instanceof FocusSubController subController) {
                boolean focusForward = subController.focusForward();
                if (focusForward)
                    return true;

            }
        }

        return false;
    }

    public boolean focusBackward() {
        if (focusTraversalComponents.isEmpty())
            return true;

        return false;
    }

//    private int findNext() {
//        if (currentFocus == -1 || currentFocus >= focusTraversalComponents.size() - 1)
//            return 0;
//
//        return currentFocus + 1;
//    }
//
//    private int findPrevious() {
//        if (currentFocus == 0)
//            return focusTraversalComponents.size() - 1;
//
//        if (currentFocus == -1)
//            return 0;
//
//        return focus - 1;
//    }

    public void setFocus(FocusableNode focusedNode) {
        for (int i = 0; i < focusTraversalComponents.size(); i++) {
            if(focusTraversalComponents.get(i) == focusedNode){
                this.focus = i;
                break;
            }
        }
    }

    public void setFocus(int focus){
        this.focus = focus;
    }

    public void resetFocus() {
        this.focus = -1;
        parentController.resetFocus();
    }
}
