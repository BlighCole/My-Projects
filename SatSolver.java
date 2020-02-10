
import java.util.*;

/**
 * A SatSolver object is capable of checking whether or not a CNFProposition is satisfiable.
 *
 * Colgate University COSC 290 Labs
 * Version 0.1,  2017
 *
 * @author Michael Hay
 * isSatHelper() and other helper functions written by Cole Bligh and Tommy Halkyard
 */
public class SatSolver {

    private int numRecursiveCalls = 0;  // incremented every time a recursive call is made

    /**
     * Construct a default CNFProposition sat solver.
     */
    public SatSolver() {}

    /**
     * Returns the number of recursive calls made during execution of isSatisfiable.
     * @return number of recursive calls made in execution of isSatisfiable.
     */
    public int getSearchCost() {
        return numRecursiveCalls;
    }

    /**
     * Checks whether the proposition is satisfiable.
     * @param phi the proposition in conjunctive normal form
     * @return true if proposition is satisfiable, false otherwise
     */
    public boolean isSatisfiable(CNFProposition phi) {
        Set<Variable> variables = new HashSet<>();
        variables.addAll(phi.getVariables());
        Model model = new Model(variables);
        return isSatHelper(phi, model);
    }

    /*private boolean isSatHelper(CNFProposition phi, Model m) {
        numRecursiveCalls++;   // please leave this line
        // implement this method so it returns the correct answer

        //Checks to see if all variables have been assigned
        Set<Variable> v = phi.getVariables();
        Set<Variable> u = new HashSet<>();
        for (Variable item: v){
          if(m.isUnassigned(item))
            u.add(item);
        }

        //base case
        if(u.size() == 0){
          Set<Clause> c = phi.getClauses();
          boolean c_temp;
          boolean eval = true;
          for (Clause item: c){
            c_temp = false;
            List<Variable> p = item.getPositiveVariables();
            List<Variable> n = item.getNegativeVariables();
            for (int i = 0; i < p.size(); i++){
              if (m.getTruthValue(p.get(i)))
                c_temp = true;
            }
            for (int index = 0; index < n.size(); index++){
              if (!m.getTruthValue(n.get(index)))
                c_temp = true;
            }
            if(eval == true && c_temp == false){
              eval = false;
              break;
            }
          }
          return eval;
        }

        //assign the first unassigned variable true and then recursive call
        Variable item = u.iterator().next();
        m.assign(item, true);
        Boolean r = isSatHelper(phi, m);

        //if proposition has been satisfied -> return true
        //otherwise change the last item to false and recursive call
        if (r == true)
          return true;
        m.unassign(item);
        m.assign(item, false);
        r = isSatHelper(phi, m);

        //if the proposition has been satisfied -> return true
        //otherwise remove the last item and return to previous recursive call
        if (r == true)
          return true;
        m.unassign(item);
        return r;
    }*/

    private boolean isSatHelper(CNFProposition phi, Model m) {
        numRecursiveCalls++;   // please leave this line
        // implement this method so it returns the correct answer

        //Checks to see if all variables have been assigned
        Set<Variable> v = phi.getVariables();
        Set<Variable> temp = unassignedValues(v, m);

        Set<Clause> c = phi.getClauses();
        if (temp.size() == v.size()){
          m = optimizationThree(phi, m);

          //optimization 4
          for (Clause part: c){
            List<Variable> alt = part.getVariables();
            List<Variable> alt_p = part.getPositiveVariables();
            List<Variable> alt_n = part.getNegativeVariables();
            Variable alt_obj = alt.iterator().next();
            if(alt.size() == 1 && !alt_p.isEmpty()){
              if(!m.isAssigned(alt_obj))
                m.assign(alt_obj, true);
              else if(!m.getTruthValue(alt_obj))
                return false;
            }else if (alt.size() == 1 && !alt_n.isEmpty()){
              if(!m.isAssigned(alt_obj))
                m.assign(alt_obj, false);
              else if(m.getTruthValue(alt_obj))
                return false;
            }
          }
          System.out.println(m);
        }

        Set<Variable> u = unassignedValues(v, m);

        //base case
        if(u.size() == 0)
          return evaluate(phi.getClauses(), m);

        //assign the first unassigned variable true and then recursive call
        Variable item = u.iterator().next();
        m.assign(item, true);
        Boolean r = isSatHelper(phi, m);

        //if proposition has been satisfied -> return true
        //otherwise change the last item to false and recursive call
        if (r == true)
          return true;
        m.unassign(item);
        m.assign(item, false);
        r = isSatHelper(phi, m);

        //if the proposition has been satisfied -> return true
        //otherwise remove the last item and return to previous recursive call
        if (r == true)
          return true;
        m.unassign(item);
        return r;
    }

    private Model optimizationThree(CNFProposition phi, Model m){
      //Optimization 3: setting pure variables
      Set<Clause> c = phi.getClauses();
      List<Variable> temp = new ArrayList<Variable>();
      List<Variable> p = new ArrayList<Variable>();
      List<Variable> n = new ArrayList<Variable>();
      List<Variable> t = new ArrayList<Variable>();
      for (Clause part: c){
        temp = part.getPositiveVariables();
        for (Variable item: temp){
          if(!p.contains(item))
            p.add(item);
        }
        temp = part.getNegativeVariables();
        for (Variable item: temp){
          if(!n.contains(item))
            n.add(item);
        }
      }

      //take the difference of p-n and n-p, then set values
      Set<Variable> pos = difference(p, n);
      Set<Variable> neg = difference(n, p);

      m = assignValue(pos, m, true);
      m = assignValue(neg, m, false);

      return m;
    }

    private Model assignValue(Set<Variable> u, Model m, boolean value){
      Iterator<Variable> temp = u.iterator();
      if(!temp.hasNext())
        return m;
      Variable obj = temp.next();
      u.remove(obj);
      m = assignValue(u, m, value);
      if(m.isUnassigned(obj))
        m.assign(obj, value);
      return m;
    }

    private Set<Variable> difference(List<Variable> a, List<Variable> b){
      Set<Variable> r = new HashSet<>();
      Iterator<Variable> temp = a.iterator();
      if (!temp.hasNext())
        return r;
      Variable o = temp.next();
      a.remove(o);
      r = difference(a, b);
      if (!b.contains(o))
        r.add(o);
      a.add(o);
      return r;
    }

    private boolean evaluate(Set<Clause> c, Model m){
      boolean eval = true;
      boolean ind = false;
      Iterator<Clause> temp = c.iterator();
      if(!temp.hasNext())
        return eval;
      Clause part = temp.next();
      c.remove(part);
      eval = evaluate(c, m);
      if(!eval)
        return eval;
      List<Variable> p = part.getPositiveVariables();
      List<Variable> n = part.getNegativeVariables();
      ind = evaluateVariables(p, m, true);
      if(ind)
        return true;
      ind = evaluateVariables(n, m, false);
      return ind;
    }

    private boolean evaluateVariables(List<Variable> v, Model m, boolean value){
      Iterator<Variable> temp = v.iterator();
      if(!temp.hasNext())
        return false;
      Variable obj = temp.next();
      v.remove(obj);
      boolean eval = evaluateVariables(v, m, value);
      if(eval)
        return eval;
      if(m.getTruthValue(obj) == value)
        return true;
      return false;
    }

    private Set<Variable> unassignedValues(Set<Variable> v, Model m){
      Set<Variable> r = new HashSet<>();
      Iterator<Variable> temp = v.iterator();
      if(!temp.hasNext())
        return r;
      Variable obj = temp.next();
      v.remove(obj);
      r = unassignedValues(v, m);
      if(m.isUnassigned(obj))
        r.add(obj);
      v.add(obj);
      return r;
    }

    public static void main(String[] args) {
        SatSolver solver = new SatSolver();
        CNFProposition proposition = CNFProposition.fromString("(p | q) & (~p | r)");
        boolean satisfiable = solver.isSatisfiable(proposition);
        System.out.println("proposition = " + proposition + " " + (satisfiable? "is": "is not") + " satisfiable.");
        //System.out.println(solver.getSearchCost());

        proposition = CNFProposition.fromString("(p | q) & (~p | ~q) & (p | ~q) & (~p | q)");
        satisfiable = solver.isSatisfiable(proposition);
        System.out.println("proposition = " + proposition + " " + (satisfiable? "is": "is not") + " satisfiable.");
        //System.out.println(solver.getSearchCost());
    }
}
