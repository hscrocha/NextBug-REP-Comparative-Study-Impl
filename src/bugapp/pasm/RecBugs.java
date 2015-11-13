/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.pasm;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Henrique
 */
public class RecBugs {
    
    public enum CalcOptions{AVERAGE, AVERAGA_COMPONENT, ACUMULATED_AVERAGE, MAX_OVERLAP};
    
    public static final int MAX_RECOMEND = 5;
    public static int GambiOracle = 0;
    public static int GambiRecs = 0;
    public static int GambiIntersect = 0;
    
    private BucketItem Main;
    private ArrayList<RecEntry> lstThreshold; //Todas Issues que passaram no Teste de Threshold
    private ArrayList<RecEntry> lstSimOverlap; //Todas Issues que passaram no Teste de Threshold e que possuem Overlap > 0
    private ArrayList<RecEntry> lstOpenOverlap; //Todas Issues que possuírem Overlap > Min
    private ArrayList<RecEntry> lstBaseline; //Issues para calcular o baseline
    
    public RecBugs(){
        this.Main = null;
        this.lstThreshold = new ArrayList<>();
        this.lstSimOverlap = new ArrayList<>();
        this.lstOpenOverlap = new ArrayList<>();
        this.lstBaseline = new ArrayList<>();
    }
    
    public void setMain(BucketItem M){
        this.Main = M;
    }
    
//    public BugPath getMain(){
//        return this.Main.getBugData();
//    }
    
    public int getMainBugId(){
        return this.Main.getBugData().getBugId();
    }
    
    public String getMainCreationDate(){
        return bugapp.util.DateUtil.getDateAsString(this.Main.getBugData().getDtCreation(),"yyyy-MM-dd");
    }
    
    public int getBaselineIssuesCount(){
        return lstBaseline.size();
    }
    
//    public ArrayList<RecEntry> getRecommendationsWithOverlap(){
//        Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
//        return lstSimOverlap;
//    }
    
    private RecEntry createEntry(BucketItem B){
        RecEntry Entry = new RecEntry();
        Entry.setBucketItem(B);
        Entry.setDistance( Main.distance(B) );
        if(Main.isSameComponent(B)){
            Entry.setSameComponent((byte)1);
        }
        else{
            Entry.setSameComponent((byte)0);
        }
        if(Main.isSameDevel(B)){        
            Entry.setSameDeveloper((byte)1);
        }
        else{
            Entry.setSameDeveloper((byte)0);
        }
        Entry.setOverlap( Main.overlap(B) );
        //Entry.setJaccard( Main.jaccard(B) );
        //Entry.setTaskCoeficient( Main.taskCoeficient(B) );
        
        return Entry;
    }

    private RecEntry createRepEntry(BucketItem B){
        RecEntry Entry = new RecEntry();
        Entry.setBucketItem(B);
        Entry.setDistance( Main.repDistance(B) );

        Entry.setSameComponent(Main.isSameComponent(B));
        Entry.setSameDeveloper(Main.isSameDevel(B));
        Entry.setSameProduct(Main.isSameProduct(B));
        Entry.setSameType(Main.isSameType(B));
        Entry.setVersionDif(Main.getVersionDiference(B));
        Entry.setPriorityDif(Main.getPriorityDiference(B));

        Entry.setOverlap( Main.overlap(B) );
        //Entry.setJaccard( Main.jaccard(B) );
        //Entry.setTaskCoeficient( Main.taskCoeficient(B) );
        
        return Entry;
    }
    
    public void addForMaxOverlap(BucketItem B){
        RecEntry Entry = createEntry(B);
        
        lstOpenOverlap.add(Entry);
        lstSimOverlap.add(Entry);
        lstThreshold.add(Entry);
        lstBaseline.add(Entry);
    }

    public void add(BucketItem B){
        RecEntry Entry = createEntry(B);
        
        if( Entry.getOverlap() > 0){
            lstOpenOverlap.add(Entry);
            lstSimOverlap.add(Entry);
        }
        lstThreshold.add(Entry);
        lstBaseline.add(Entry);
    }
    
    public boolean addThresholdOverlap(BucketItem B, float SimThreshold, float MinOverlap){
        RecEntry Entry = createEntry(B);
        float Dist = Entry.getDistance();

        lstBaseline.add(Entry);
        if (Entry.getOverlap() >= MinOverlap) {
            lstOpenOverlap.add(Entry);
        }

        if (Dist < SimThreshold) {

            lstThreshold.add(Entry);
            if (Entry.getOverlap() > 0) {
                lstSimOverlap.add(Entry);
            }

            return true;
        }
        return false;
    }
    
    public boolean addForRep(BucketItem B, float SimThreshold, float MinOverlap){
        RecEntry Entry = createRepEntry(B);
        float Dist = Entry.getDistance();

        lstBaseline.add(Entry);
        if(Entry.getOverlap() >= MinOverlap) {
            lstOpenOverlap.add(Entry);
        }

        if(Dist < SimThreshold) {

            lstThreshold.add(Entry);
            if (Entry.getOverlap() > 0) {
                lstSimOverlap.add(Entry);
            }

            return true;
        }
        return false;
    }

    public boolean addSameCompThresholdOverlap(BucketItem B, float SimThreshold, float MinOverlap){
        RecEntry Entry = createEntry(B);
        float Dist = Entry.getDistance();
        
        if(Main.isSameComponent(B)){
            lstBaseline.add(Entry);
            if (Entry.getOverlap() >= MinOverlap) {
                lstOpenOverlap.add(Entry);
            }

            if (Dist < SimThreshold) {

                lstThreshold.add(Entry);
                if (Entry.getOverlap() > 0) {
                    lstSimOverlap.add(Entry);
                }

                return true;
            }
        }
        return false;
    }
    
    public boolean addSameComp(BucketItem B){
        RecEntry Entry = createEntry(B);
        float Dist = Entry.getDistance();
        
        if(Main.isSameComponent(B)){
            lstBaseline.add(Entry);
            if (Entry.getOverlap() > 0) {
                lstOpenOverlap.add(Entry);
            }

            lstThreshold.add(Entry);
            if (Entry.getOverlap() > 0) {
                lstSimOverlap.add(Entry);
            }
            return true;
        }
        return false;

    }
    
    public boolean addSameCompThreshold(BucketItem B, float Threshold){
        RecEntry Entry = createEntry(B);
        float Dist = Entry.getDistance();
        
        if(Main.isSameComponent(B)){
            lstBaseline.add(Entry);
            if (Entry.getOverlap() > 0) {
                lstOpenOverlap.add(Entry);
            }

            if (Dist < Threshold) {

                lstThreshold.add(Entry);
                if (Entry.getOverlap() > 0) {
                    lstSimOverlap.add(Entry);
                }

                return true;
            }
        }
        return false;
    }
        
    public boolean addIfPassThreshold(BucketItem B, float Threshold){
        RecEntry Entry = createEntry(B);
        float Dist = Entry.getDistance();
        
        if( Entry.getOverlap() > 0){
            lstOpenOverlap.add(Entry);
        }
        lstBaseline.add(Entry);

        if (Dist < Threshold) {
            lstThreshold.add(Entry);
            if(Entry.getOverlap()>0){
                lstSimOverlap.add(Entry);
            }
            return true;
        }
        return false;
    }
    
    public AverageBucketMetrics[] calc(CalcOptions Option){
        switch(Option){
            //case MAX_OVERLAP: return calcMaxOverlap(); 
            //case AVERAGA_COMPONENT: return calcAverageComponent();
            //case ACUMULATED_AVERAGE: return calcAcumulatedAverage();
            default: return calcAverage();
        }
    }

    public float[] calcPrecisionOld(){
        int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] Precision = new float[Max];

        Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
                
        //Criar os objetos do vetor
        for(int i=0; i<Precision.length; i++){
            Precision[i]=0;
        }
        
        //Calcular a precisão
        for(int k=0; k<Max; k++){
            RecEntry SimItem = lstSimOverlap.get(k);
            
            for(int v=0;v<k+1 && v<lstOpenOverlap.size(); v++){ //Verificar a precisão sem se preocupar com a ordem
                RecEntry OvItem = lstOpenOverlap.get(v);
                if(SimItem == OvItem){ //True Positive,
                    //Atualizar as estatisticas
                    for(int i=k; i<Precision.length; i++){
                        Precision[i]++;
                    }
                    break;
                }
            }
        }
        
//        if(this.Main.getBugData().getBugId()==474257){
//            System.out.print("Prec=");
//            System.out.println(java.util.Arrays.toString(Precision));
//        }
        //Calcula a média
        for(int i=0; i<Precision.length; i++){
            float d = i+1;
            Precision[i]/=d;
        }

        return Precision;
    }
    
    public float[] calcPrecision(){
        return calcPrecision(false);
    }
    
    public float[] calcRecsIntersectOracle(boolean Baseline){
        int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] Intersect = new float[Max];

        if(!Baseline){ 
            Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        }
        else{
            Collections.sort(lstSimOverlap, new BugIdComparator()); //Ordena pela Data Decrescente (Aq)
        }
        //Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
                
        //Criar os objetos do vetor
        for(int i=0; i<Intersect.length; i++){
            Intersect[i]=0;
        }
        
        //Calcular a Interseção entre o conjunto de Recs e o conjunto do Oraculo
        for(int k=0; k<Max; k++){
            RecEntry SimItem = lstSimOverlap.get(k); //Aq(k)
            if(SimItem.getOverlap() >= PasmAdhoc.OverlapThreshold){ //True Positive
                for (int i = k; i < Intersect.length; i++) {
                    Intersect[i]++;
                }
            }
        }
        
        return Intersect;
    }
    
    public float[] calcPrecision(boolean Baseline){
        int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] Precision = new float[Max];

        if(!Baseline){ 
            Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        }
        else{
            Collections.sort(lstSimOverlap, new BugIdComparator()); //Ordena pela Data Decrescente (Aq)
        }
        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
                
        //Criar os objetos do vetor
        for(int i=0; i<Precision.length; i++){
            Precision[i]=0;
        }
        
        //Calcular a precisão
        for(int k=0; k<Max; k++){
            RecEntry SimItem = lstSimOverlap.get(k); //Aq(k)
            if(SimItem.getOverlap() > PasmAdhoc.OverlapThreshold){ //True Positive
                for (int i = k; i < Precision.length; i++) {
                    Precision[i]++;
                }
            }
// É possiv q essa porra estava com trem tosco (giria para ***)
//            for(int v=0; v<lstOpenOverlap.size() && v<Max; v++){ //Verificar a precisão sem se preocupar com a ordem
//                RecEntry OvItem = lstOpenOverlap.get(v); //Eq
//                if(SimItem == OvItem){ //True Positive,
//                    //Atualizar as estatisticas
//                    for(int i=k; i<Precision.length; i++){
//                        Precision[i]++;
//                    }
//                    break;
//                }
//            }
        }
        
        //Calcula a média
        for(int i=0; i<Precision.length; i++){
            float d = i+1;
            Precision[i]/=d;
        }

        return Precision;
    }

    public float[][] calcPrecisionWithBaseline(){
        int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[][] Precision = new float[2][Max];

        Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
        //Collections.sort(lstBaseline, new BugIdComparator()); //Ordena pela Data Decrescente (Aq)
                
        //Criar os objetos do vetor
        for(int i=0; i<Precision[0].length; i++){
            Precision[0][i]=0;
            Precision[1][i]=0;
        }
        
        //Calcular a precisão
        for(int k=0; k<Max; k++){
            RecEntry SimItem = lstSimOverlap.get(k); //Aq(k)
            
            //Normal Prec.
            for(int v=0; v<lstOpenOverlap.size() && v<Max; v++){ //Verificar a precisão sem se preocupar com a ordem
                RecEntry OvItem = lstOpenOverlap.get(v); //Eq
                if(SimItem == OvItem){ //True Positive,
                    //Atualizar as estatisticas
                    for(int i=k; i<Precision[0].length; i++){
                        Precision[0][i]++;
                    }
                    break;
                }
            }
            
            //Baseline Prec
            RecEntry BaseItem = lstBaseline.get(k); //Aq(k)
            for(int v=0; v<lstOpenOverlap.size() && v<Max; v++){ //Verificar a precisão sem se preocupar com a ordem
                RecEntry OvItem = lstOpenOverlap.get(v); //Eq
                if(BaseItem == OvItem){ //True Positive,
                    //Atualizar as estatisticas
                    for(int i=k; i<Precision[1].length; i++){
                        Precision[1][i]++;
                    }
                    break;
                }
            }
        }
        
        //Calcula a média
        for(int i=0; i<Precision[0].length; i++){
            float d = i+1;
            Precision[0][i]/=d;
            Precision[1][i]/=d;
        }

        return Precision;
    }
    
    public float[] calcRecallOld(){
        int MaxOv = lstOpenOverlap.size() < MAX_RECOMEND ? lstOpenOverlap.size() : MAX_RECOMEND;
        int MaxSim = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] Recall = new float[MaxOv];

        Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
        
        //Criar os objetos do vetor
        for(int i=0; i<Recall.length; i++){
            Recall[i]=0;
        }
        
        //Calcular a precisão
        for(int k=0; k<MaxOv; k++){
           RecEntry OvItem = lstOpenOverlap.get(k);
            
            for(int v=0; v<k+1 && v<MaxSim; v++){ //Verificar a precisão sem se preocupar com a ordem
                RecEntry SimItem = lstSimOverlap.get(v);
                if(SimItem == OvItem){ //True Positive,
                    //Atualizar as estatisticas
                    for(int i=k; i<Recall.length; i++){
                        Recall[i]++;
                    }
                    break;
                }
            }
        }
        
        //Calcula a média
        for(int i=0; i<Recall.length; i++){
            float d = i+1;
            Recall[i]/=d;
        }

        return Recall;
    }

    public float[] calcRecall(){
        return calcRecall(false);
    }

    public float[] calcRecall(boolean Baseline){
        //int MaxOv = lstOpenOverlap.size() < MAX_RECOMEND ? lstOpenOverlap.size() : MAX_RECOMEND;
        int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] Recall = new float[Max];

        if(!Baseline){ 
            Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        }
        else{
            Collections.sort(lstSimOverlap, new BugIdComparator()); //Ordena pela Data Descrescente (Aq)
        }
        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
        
        //Criar os objetos do vetor
        for(int i=0; i<Recall.length; i++){
            Recall[i]=0;
        }
        
        //Calcular o recall
        for(int k=0; k<Max; k++){
            RecEntry SimItem = lstSimOverlap.get(k); //Aq(k)
            
            for(int v=0; v<lstOpenOverlap.size(); v++){ //Verificar o recall sem se preocupar com a ordem
                RecEntry OvItem = lstOpenOverlap.get(v); //Eq
                if(SimItem == OvItem){ //True Positive,
                    //Atualizar as estatisticas
                    for(int i=k; i<Recall.length; i++){
                        Recall[i]++;
                    }
                    break;
                }
            }
        }
        
        float d = (float) lstOpenOverlap.size();
        //Calcula a média
        for(int i=0; i<Recall.length; i++){
            Recall[i]/=d;
        }
        return Recall;
    }
    
    public float[] calcMaxRecall(){
        int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] MaxRecall = new float[Max];
        float Oq = (float) lstOpenOverlap.size();
        
        //Calcula Recall como se todos os *i* da lista eram relevantes
        float f;
        int i;
        for(i=0, f=1; i<MaxRecall.length; i++, f++){
            MaxRecall[i]= i<lstOpenOverlap.size()? f/Oq : 1;
        }
        return MaxRecall;
    }

    public float[] calcFeedback(boolean OnlyRecsWithOverlap){
        int Max;
        if(OnlyRecsWithOverlap)
            Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        else
            Max = lstThreshold.size() < MAX_RECOMEND ? lstThreshold.size() : MAX_RECOMEND;
        
        float[] Feedback = new float[Max];
        
        //Calcula Recall como se todos os *i* da lista eram relevantes
        int i;
        for(i=0; i<Feedback.length; i++){
            Feedback[i]=1;
        }
        return Feedback;
    }

    public float[] calcExternalOverlap(){
        int Max = lstOpenOverlap.size() < MAX_RECOMEND ? lstOpenOverlap.size() : MAX_RECOMEND;
        //int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] ExtOv = new float[Max];

        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
        
        //Criar os objetos do vetor
        java.util.Arrays.fill(ExtOv, 0f);
        
        //Calcular o recall
        for (int v = 0; v < lstOpenOverlap.size(); v++) { //Verificar sem se preocupar com a ordem
            RecEntry OvItem = lstOpenOverlap.get(v); //Eq
            for(int k= v; k < ExtOv.length; k++){
                ExtOv[k]+= OvItem.getOverlap();
            }
        }
        
        //Calcula a média
        for(int i=0; i<ExtOv.length; i++){
            float d = i+1;
            ExtOv[i]/=d;
        }
        return ExtOv;
    }    
    
    public float[] calcMaxExternalOverlap(){
        int Max = lstOpenOverlap.size() < MAX_RECOMEND ? lstOpenOverlap.size() : MAX_RECOMEND;
        //int Max = lstSimOverlap.size() < MAX_RECOMEND ? lstSimOverlap.size() : MAX_RECOMEND;
        float[] ExtOv = new float[Max];

        Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
        
        //Criar os objetos do vetor
        java.util.Arrays.fill(ExtOv, 0f);
        
        if (!lstOpenOverlap.isEmpty()) {
            RecEntry OvItem = lstOpenOverlap.get(0); //Eq
            for (int k = 0; k < ExtOv.length; k++) {
                ExtOv[k] = OvItem.getOverlap();
            }
        }
        return ExtOv;
    }    
    
    public AverageBucketMetrics[] calcMaxOverlap(){
        int Max = lstThreshold.size() < MAX_RECOMEND ? lstThreshold.size() : MAX_RECOMEND;
        AverageBucketMetrics M[]=new AverageBucketMetrics[Max];
        Collections.sort(lstThreshold); //Ordena pela Similaridade

        //Criar os objetos do vetor
        for(int i=0; i<M.length; i++){
            M[i]=new AverageBucketMetrics();
        }

        //Somar as estasticias para cada Top-1, Top-2,... até Top-20.
        for(int k=0; k<lstThreshold.size() && k<M.length; k++){
            RecEntry Item = lstThreshold.get(k);
            //M[k].IssueCount=k+1;
            for(int i=k; i<M.length && i<lstThreshold.size(); i++){
                if(M[i].Overlap < Item.getOverlap()){
                    M[i].Similarity = Item.getSimilarity();
                    M[i].Components = Item.getSameComponent();
                    M[i].Developers = Item.getSameDeveloper();
                    M[i].Overlap = Item.getOverlap();
                    M[i].Jaccard = Item.getJaccard();
                }
            }
        }
        
        return M;
    }

    public AverageBucketMetrics[] calcAverageComponent(){
        
        int Max = lstThreshold.size() < MAX_RECOMEND ? lstThreshold.size() : MAX_RECOMEND;
        
        AverageBucketMetrics M[]=new AverageBucketMetrics[Max];
        Collections.sort(lstThreshold); //Ordena pela Similaridade
        
        //Criar os objetos do vetor
        float[] OverlDivisor = new float[Max];
        float[] Divisor = new float[Max];
        for(int i=0; i<M.length; i++){
            M[i]=new AverageBucketMetrics();
            OverlDivisor[i]=0;
            Divisor[i]=0;
        }
        
        //Somar as estasticias para cada Top-1, Top-2,... até Top-20.
        for(int k=0; k<lstThreshold.size() && k<M.length; k++){
            RecEntry Item = lstThreshold.get(k);
            //M[k].IssueCount=k+1;
            if (Item.getSameComponent() == 1) {
                for (int i = k; i < M.length && i < lstThreshold.size(); i++) {
                    Divisor[i]++;
                    M[i].Similarity += Item.getSimilarity();
                    M[i].Components += Item.getSameComponent();
                    M[i].Developers += Item.getSameDeveloper();
                    if (Item.getOverlap() >= 0) {
                        M[i].Overlap += Item.getOverlap();
                        M[i].Jaccard += Item.getJaccard();
                        OverlDivisor[i]++;
                    }
                }
            }
        }
        
        //Calcular a média para cada stat
        for(int i=0; i<M.length; i++){
            if(Divisor[i]>0){
                M[i].Similarity /= Divisor[i];
                M[i].Components /= Divisor[i];
                M[i].Developers /= Divisor[i];
                if (OverlDivisor[i] > 0) {
                    M[i].Overlap /= OverlDivisor[i];
                    M[i].Jaccard /= OverlDivisor[i];
                } else {
                    M[i].Overlap = -1;
                    M[i].Jaccard = -1;
                }
            }
            else{
                M[i].Similarity = -1;
                M[i].Components = -1;
                M[i].Developers = -1;
                M[i].Overlap = -1;
                M[i].Jaccard = -1;
            }
        }
        return M;
    }
    
    public AverageBucketMetrics[] calcAverage(){
        int Max = lstThreshold.size() < MAX_RECOMEND ? lstThreshold.size() : MAX_RECOMEND;
        
        AverageBucketMetrics M[]=new AverageBucketMetrics[Max];
        Collections.sort(lstThreshold); //Ordena pela Similaridade
        
        //Criar os objetos do vetor
        float[] OverlDivisor = new float[Max];
        for(int i=0; i<M.length; i++){
            M[i]=new AverageBucketMetrics();
            OverlDivisor[i]=0;
        }
        
        //Somar as estasticias para cada Top-1, Top-2,... até Top-20.
        for(int k=0; k<lstThreshold.size() && k<M.length; k++){
            RecEntry Item = lstThreshold.get(k);
            //M[k].IssueCount=k+1;
            for(int i=k; i<M.length && i<lstThreshold.size(); i++){
                M[i].Similarity+=Item.getSimilarity();
                M[i].Components+=Item.getSameComponent();
                M[i].Developers+=Item.getSameDeveloper();
                if(Item.getOverlap()>=0){
                    M[i].Overlap+=Item.getOverlap();
                    M[i].Jaccard+=Item.getJaccard();
                    M[i].TaskCoeficient+=Item.getTaskCoeficient();
                    
                    OverlDivisor[i]++;
//                    if(M[i].MaxOverlap == 0 || M[i].MaxOverlap < Item.getOverlap()){
//                        M[i].MaxOverlap = Item.getOverlap();
//                    }
                }
            }
        }
        
        //Calcular a média para cada stat
        for(int i=0; i<M.length; i++){
            float d = i+1;
            M[i].Similarity/=d;
            M[i].Components/=d;
            M[i].Developers/=d;
            if(M[i].Developers > 0){
                M[i].DevLikelihood = 1;
            }
            if(OverlDivisor[i]>0){
                M[i].Overlap/=OverlDivisor[i];
                M[i].Jaccard/=OverlDivisor[i];
                M[i].TaskCoeficient/=OverlDivisor[i];
            }
            else{
                M[i].Overlap = -1;
                M[i].Jaccard = -1;
                M[i].TaskCoeficient = -1;
                //M[i].MaxOverlap = -1; //Desnecessario
            }
        }
        
        float intersectRecsxOracle[] = calcRecsIntersectOracle(false);
        float feedback[] = calcFeedback(true);
        float divPrec;
        float divRecall = (float) lstOpenOverlap.size();
        for(int i=0; i<M.length; i++){
            M[i].Feedback = (i<feedback.length)?feedback[i]:0;
            
            if(i<intersectRecsxOracle.length){
                divPrec = i+1;
                M[i].Precision = intersectRecsxOracle[i] / divPrec;
                M[i].Likelihood = intersectRecsxOracle[i] > 0 ? 1 : 0;
                M[i].Recall = intersectRecsxOracle[i] / divRecall;
                M[i].MaxRecall = divPrec<divRecall ? divPrec / divRecall : 1f;
            }
            else{
                M[i].Precision=-1;
                M[i].Likelihood=-1;
                M[i].Recall=-1;
                M[i].MaxRecall = -1;
            }
        }
        
        
        if(lstSimOverlap.isEmpty()){
            //System.out.printf(" -- BugId: %d, Recs size: %d \n", this.getMainBugId(), 0);
            GambiRecs++;
        }
        if(lstOpenOverlap.isEmpty()){
            GambiOracle++;
        }

//        float P[] = calcPrecision();
//        for(int i=0; i<M.length; i++){
//            if(i<P.length){
//                M[i].Precision = P[i];
//                M[i].Likelihood = P[i]>0?1:0;
//            }
//            else{
//                M[i].Precision=-1;
//                M[i].Likelihood=-1;
//            }
//        }
//
//        float R[] = calcRecall();
//        for(int i=0; i<M.length; i++){
//            if(i<R.length){
//                M[i].Recall = R[i];
//            }
//            else{
//                M[i].Recall=-1;
//            }
//        }
//        
//        float MaxR[] = calcMaxRecall();
//        for(int i=0; i<M.length; i++){
//            if(i<MaxR.length){
//                M[i].setMaxRecall(MaxR[i]);
//            }else{
//                M[i].setMaxRecall(-1);
//            }
//        }
//        
//        float ExtOv[] = calcExternalOverlap();
//        for(int i=0; i<M.length; i++){
//            if(i<ExtOv.length){
//                M[i].ExternalOverlap = ExtOv[i];
//            }
//            else{
//                M[i].ExternalOverlap = -1;
//            }
//        }
//
//        float MaxExtOv[] = calcMaxExternalOverlap();
//        for(int i=0; i<M.length; i++){
//            if(i<MaxExtOv.length){
//                M[i].MaxOverlap = MaxExtOv[i];
//            }
//            else{
//                M[i].MaxOverlap = -1;
//            }
//        }
        
        return M;
    }
    
    
    public AverageBucketMetrics[] calcAcumulatedAverage(){
        AverageBucketMetrics[] OldMet = calcAverage();
        
        AverageBucketMetrics[] AccMet=new AverageBucketMetrics[MAX_RECOMEND];
        
        //Criar os objetos do vetor
        for(int i=0; i<OldMet.length; i++){
            AccMet[i]=new AverageBucketMetrics();
            
            AccMet[i].Similarity = OldMet[i].Similarity;
            AccMet[i].Components = OldMet[i].Components;
            AccMet[i].Developers = OldMet[i].Developers;
            AccMet[i].DevLikelihood = OldMet[i].DevLikelihood;

            AccMet[i].Overlap = OldMet[i].Overlap;
            AccMet[i].Jaccard = OldMet[i].Jaccard;
            AccMet[i].Overlap = OldMet[i].Overlap;
            AccMet[i].Recall = OldMet[i].Recall;

            AccMet[i].Precision = OldMet[i].Precision;
            if(i!=0 && AccMet[i].Precision<0){
                AccMet[i].Precision = OldMet[i-1].Precision;
            }
            AccMet[i].Likelihood = OldMet[i].Likelihood;
            if(i!=0 && AccMet[i].Likelihood<0){
                AccMet[i].Likelihood = OldMet[i-1].Likelihood;
            }

            AccMet[i].BaselinePrecision = OldMet[i].BaselinePrecision;
            if(i!=0 && AccMet[i].BaselinePrecision<0){
                AccMet[i].BaselinePrecision = OldMet[i-1].BaselinePrecision;
            }
            AccMet[i].BaselineLikelihood = OldMet[i].BaselineLikelihood;
            if(i!=0 && AccMet[i].BaselineLikelihood<0){
                AccMet[i].BaselineLikelihood = OldMet[i-1].BaselineLikelihood;
            }
        }
        
        for(int i=OldMet.length; i<AccMet.length; i++){
            AccMet[i] = AccMet[i-1];
        }
        
        return AccMet;
    }
    
    public AverageBucketMetrics[] baselineMetrics(CalcOptions Option){
        switch(Option){
            //case MAX_OVERLAP: return calcMaxOverlap(); 
            //case AVERAGA_COMPONENT: return calcAverageComponent();
            case ACUMULATED_AVERAGE: return baselineAcumulatedAverage();
            default: return baselineAverage();
        }
    }
    
    public AverageBucketMetrics[] baselineAverage(){
        
        int Max = lstThreshold.size() < MAX_RECOMEND ? lstThreshold.size() : MAX_RECOMEND;
        
        AverageBucketMetrics M[]=new AverageBucketMetrics[Max];
        //Baseline não Ordena
        //Collections.sort(lstThreshold); //Ordena pela Similaridade
        
        //Criar os objetos do vetor
        float[] OverlDivisor = new float[Max];
        for(int i=0; i<M.length; i++){
            M[i]=new AverageBucketMetrics();
            OverlDivisor[i]=0;
        }
        
        //Somar as estasticias para cada Top-1, Top-2,... até Top-20.
        for(int k=0; k<lstThreshold.size() && k<M.length; k++){
            RecEntry Item = lstThreshold.get(k);
            //M[k].IssueCount=k+1;
            for(int i=k; i<M.length && i<lstThreshold.size(); i++){
                M[i].Similarity+=Item.getSimilarity();
                M[i].Components+=Item.getSameComponent();
                M[i].Developers+=Item.getSameDeveloper();
                if(Item.getOverlap()>=0){
                    M[i].Overlap+=Item.getOverlap();
                    M[i].Jaccard+=Item.getJaccard();
                    M[i].TaskCoeficient+=Item.getTaskCoeficient();
                    
                    OverlDivisor[i]++;
//                    if(M[i].MaxOverlap == 0 || M[i].MaxOverlap < Item.getOverlap()){
//                        M[i].MaxOverlap = Item.getOverlap();
//                    }
                }
            }
        }
        
        //Calcular a média para cada stat
        for(int i=0; i<M.length; i++){
            float d = i+1;
            M[i].Similarity/=d;
            M[i].Components/=d;
            M[i].Developers/=d;
            if(M[i].Developers > 0){
                M[i].DevLikelihood = 1;
            }
            if(OverlDivisor[i]>0){
                M[i].Overlap/=OverlDivisor[i];
                M[i].Jaccard/=OverlDivisor[i];
                M[i].TaskCoeficient/=OverlDivisor[i];
            }
            else{
                M[i].Overlap = -1;
                M[i].Jaccard = -1;
                M[i].TaskCoeficient = -1;
                //M[i].MaxOverlap = -1; //Desnecessario
            }
        }
        
        float P[] = calcPrecision(true);
        for(int i=0; i<M.length; i++){
            if(i<P.length){
                M[i].Precision = P[i];
                M[i].Likelihood = P[i]>0?1:0;
            }
            else{
                M[i].Precision=-1;
                M[i].Likelihood=-1;
            }
        }

        float R[] = calcRecall(true);
        for(int i=0; i<M.length; i++){
            if(i<R.length){
                M[i].Recall = R[i];
            }
            else{
                M[i].Recall=-1;
            }
        }
        
        float ExtOv[] = calcExternalOverlap();
        for(int i=0; i<M.length; i++){
            if(i<ExtOv.length){
                M[i].ExternalOverlap = ExtOv[i];
            }
            else{
                M[i].ExternalOverlap = -1;
            }
        }

        float MaxExtOv[] = calcMaxExternalOverlap();
        for(int i=0; i<M.length; i++){
            if(i<MaxExtOv.length){
                M[i].MaxOverlap = MaxExtOv[i];
            }
            else{
                M[i].MaxOverlap = -1;
            }
        }
        
        return M;
    }
    
    public AverageBucketMetrics[] baselineAcumulatedAverage(){
        AverageBucketMetrics[] OldMet = baselineAverage();
        
        AverageBucketMetrics[] AccMet=new AverageBucketMetrics[MAX_RECOMEND];
        
        //Criar os objetos do vetor
        for(int i=0; i<OldMet.length; i++){
            AccMet[i]=new AverageBucketMetrics();
            
            AccMet[i].Similarity = OldMet[i].Similarity;
            AccMet[i].Components = OldMet[i].Components;
            AccMet[i].Developers = OldMet[i].Developers;
            AccMet[i].DevLikelihood = OldMet[i].DevLikelihood;

            AccMet[i].Overlap = OldMet[i].Overlap;
            AccMet[i].Jaccard = OldMet[i].Jaccard;
            AccMet[i].Overlap = OldMet[i].Overlap;
            AccMet[i].Recall = OldMet[i].Recall;

            AccMet[i].Precision = OldMet[i].Precision;
            if(i!=0 && AccMet[i].Precision<0){
                AccMet[i].Precision = OldMet[i-1].Precision;
            }
            AccMet[i].Likelihood = OldMet[i].Likelihood;
            if(i!=0 && AccMet[i].Likelihood<0){
                AccMet[i].Likelihood = OldMet[i-1].Likelihood;
            }
        }
        
        for(int i=OldMet.length; i<AccMet.length; i++){
            AccMet[i] = AccMet[i-1];
        }
        
        return AccMet;
    }
    
    public int getRecommendationsCount(){
        return lstThreshold.size();
    }
    
    public int getRecommendationsCountWithOverlap(){
        return lstSimOverlap.size();
    }
    
    @Override
    public String toString(){
        StringBuilder stb=new StringBuilder();
        stb.append("=== Main: ");
        stb.append(Main.getBugData().getBugId());
        stb.append(",");
        stb.append(Main.getBugData().getShortDesc());
        stb.append("\n");
        
        for(int i=0; i<lstSimOverlap.size() && i<MAX_RECOMEND; i++){
            stb.append("   ");
            stb.append( lstSimOverlap.get(i).toString());
            stb.append("\n");
        }
        return stb.toString();
    }

    public String toMozillaDailyLogString(){
        if(Main.getBugData().getDeveloperName().equals("nobody@mozilla.org")){
            return "";
        }
        
        StringBuilder stb=new StringBuilder();
        stb.append("=== Main: ");
        stb.append(Main.getBugData().getShortDesc());
        //"=HYPERLINK(\"https://bugzilla.mozilla.org/show_bug.cgi?id=1013036\",\"1013036\")"
        stb.append(", https://bugzilla.mozilla.org/user_profile?login=");
        stb.append(Main.getBugData().getDeveloperName().replaceAll("@", "%40"));
        stb.append(" \n=HYPERLINK(\"https://bugzilla.mozilla.org/show_bug.cgi?id=");
        //stb.append(", https://bugzilla.mozilla.org/show_bug.cgi?id=");
        stb.append(Main.getBugData().getBugId());
        stb.append("\",\"");
        stb.append(Main.getBugData().getBugId());
        stb.append("\") \n\n");
        
        Collections.sort(lstThreshold); //Ordena pela Similaridade
        for(int i=0; i<lstThreshold.size() && i<MAX_RECOMEND; i++){
            RecEntry Item = lstThreshold.get(i);
            stb.append("   ");
            stb.append( Item.getSimilarity() );
            stb.append(", ");
            stb.append( Item.getIssueData().getDtCreation() );
            stb.append(", ");
            stb.append(Item.getIssueData().getShortDesc());
            stb.append(", d=");
            stb.append(Item.getSameDeveloper());
            
            stb.append(", =HYPERLINK(\"https://bugzilla.mozilla.org/show_bug.cgi?id=");
            //stb.append(", https://bugzilla.mozilla.org/show_bug.cgi?id=");
            stb.append(Item.getIssueData().getBugId());
            stb.append("\",\"");
            stb.append(Item.getIssueData().getBugId());
            stb.append("\") \n");

            //stb.append(", https://bugzilla.mozilla.org/show_bug.cgi?id=");
            //stb.append( Item.getIssueData().getBugId() );
            stb.append(" \n");
          
//            stb.append( lstThreshold.get(i).toString());
        }
        return stb.toString();
    }
    
    public DisperseMetricEntity calcDisperseMetrics(int TopMax){
        int Max = (lstSimOverlap.size() < TopMax)? lstSimOverlap.size() : TopMax; 
        DisperseMetricEntity Met = new DisperseMetricEntity();

        Collections.sort(lstSimOverlap); //Ordena pela Similaridade (Aq)
        float simavg = 0, simmax = 0, ovlavg = 0, ovlmax = 0, divisor = (float) Max;
        for(int i=0; i<Max; i++){
             RecEntry Rec = lstSimOverlap.get(i);
             Met.appendDetail( this.getMainBugId() );
             Met.appendDetail(";");
             Met.appendDetail( Max );
             Met.appendDetail(";");
             Met.appendDetail( Rec.getIssueData().getBugId() );
             Met.appendDetail(";");
             Met.appendDetail( Rec.getSimilarity() );
             Met.appendDetail(";");
             Met.appendDetail( Rec.getOverlap() );
             Met.appendDetail("\n");
             
             simavg += Rec.getSimilarity();
             ovlavg += Rec.getOverlap();
                
             if(simmax < Rec.getSimilarity()){
                simmax = Rec.getSimilarity();
             }
                
             if(ovlmax < Rec.getOverlap()){
                 ovlmax = Rec.getOverlap();
             }
        }
        
        Met.setAverageOverlap(ovlavg/divisor);
        Met.setAverageSimilarity(simavg/divisor);
        
        Met.setMaxOverlap(ovlmax);
        Met.setMaxSimilarity(simmax);
        
        return Met;
    }
    
//    public int getOpenBugWithOverlapCount(){
//        return lstOpenOverlap.size();
//    }
    
    public String toCsvStringMaxOverlap(){
        RecEntry MaxOv = getOpenBugWithMaxOverlap();
        RecEntry Aq1 = getMaxSimWithOverlap();
        
        if (MaxOv != null && Aq1 != null) {
            StringBuilder stb = new StringBuilder();

            stb.append(this.getMainBugId());
            stb.append(";");
            stb.append(this.Main.getBugData().getDtCreation());
            stb.append(";");

            stb.append(MaxOv.getIssueData().getBugId());
            stb.append(";");
            stb.append(MaxOv.getOverlap());
            stb.append(";");
            //stb.append(lstOpenOverlap.size());
            stb.append(lstThreshold.size());
            stb.append(";");
            stb.append(Aq1.getOverlap());
            stb.append("\n");
            return stb.toString();
        }
        else{
            return null;
        }
    }
    
    private RecEntry getOpenBugWithMaxOverlap(){
        if(!lstOpenOverlap.isEmpty()){
            //System.out.println(lstOpenOverlap.size());
            Collections.sort(lstOpenOverlap, new OverlapComparator()); //ordena pelo Overlap (Eq)
            return lstOpenOverlap.get(0);
        }
        else{
            return null;
        }
    }

    private RecEntry getMaxSimWithOverlap(){
        if(!lstSimOverlap.isEmpty()){
            //System.out.println(lstOpenOverlap.size());
            Collections.sort(lstSimOverlap); //ordena pela Sim
            return lstSimOverlap.get(0);
        }
        else{
            return null;
        }
    }
    
    
}
