package models.tools

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html


// Returned to the View if a tool is requested with the getTool route
case class Toolitem(toolname : String,
                    toolnameLong : String,
                    toolnameAbbrev : String,
                    category : String,
                    optional : String,
                    params : Seq[(String, Seq[Param])])

// Specification of the internal representation of a Tool
case class Tool(toolNameShort: String,
                toolNameLong: String,
                toolNameAbbrev: String,
                category: String,
                optional: String,
                params: Map[String, Param],
                toolitem: Toolitem,
                paramGroups: Map[String, Seq[String]])


// Class which provides access to all Tools
@Singleton
final class ToolFactory @Inject() (paramAccess: ParamAccess) {


  def getResults(jobID : String, toolname: String, jobPath: String)(implicit request: Request[AnyContent]): Seq[(String, Html)] = {
    toolname match {
      case "psiblast" => Seq(("Hitlist", views.html.jobs.resultpanels.psiblast.hitlist(jobID)))

      case "hhblits" => Seq(("Hitlist", views.html.jobs.resultpanels.hhblits.hitlist(jobID)),
        ("Full_Alignment", views.html.jobs.resultpanels.alignedit("full_alignment", s"/files/$jobID/out.full.fas")),
        ("Reduced_Alignment", views.html.jobs.resultpanels.alignedit("reduced_alignment", s"/files/$jobID/out.reduced.fas")))

      case "marcoil" => Seq(("CC-Prob", views.html.jobs.resultpanels.image(s"/files/$jobID/alignment_ncoils.png")),
        ("ProbState", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/alignment.ProbPerState")),
        ("Domains", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/alignment.Domains")),
        ("ProbList/PSSM", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/alignment.ProbList")))

      case "modeller" => Seq(("3D-Structure", views.html.jobs.resultpanels.NGL3DStructure(s"/files/$jobID/$jobID.pdb")),
        ("VERIFY3D", views.html.jobs.resultpanels.modeller(s"/files/$jobID/$jobID.verify3d.png", s"$jobPath$jobID/results/verify3d/$jobID.plotdat")),
        ("SOLVX", views.html.jobs.resultpanels.modeller(s"/files/$jobID/$jobID.solvx.png", s"$jobPath$jobID/results/solvx/$jobID.solvx")),
        ("ANOLEA", views.html.jobs.resultpanels.modeller(s"/files/$jobID/$jobID.anolea.png", s"$jobPath$jobID/results/$jobID.pdb.profile")))

      case "protblast" => Seq(("Hits", views.html.jobs.resultpanels.blastviz_extra(jobID)), ("E-Values", views.html.jobs.resultpanels.evalues(jobID)),
        ("Fasta", views.html.jobs.resultpanels.fasta(jobID)), ("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_psiblast(jobID)))

      case "tcoffee" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)), ("Conservation", views.html.jobs.resultpanels.tcoffee_colored(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")), ("Text", views.html.jobs.resultpanels.tcoffee_text(jobID)))

      case "hmmer" => Seq(("Results", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/outfile")),
        ("Stockholm", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/outfile_multi_sto")),
        ("Domain_Table", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/domtbl")))

      case "hhpred" => Seq(("Hitlist", views.html.jobs.resultpanels.hhpred.hitlist(jobID)),
        ("FullAlignment", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)))

      case "hhpred_manual" => Seq(("Results", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/results.out")),
        ("PIR", views.html.jobs.resultpanels.hhpred.forward(s"$jobPath$jobID/results/tomodel.pir", jobID)))

      case "ancescon" => Seq(("Tree", views.html.jobs.resultpanels.tree(s"$jobPath$jobID/results/alignment2.clu.tre", "ancescon_div")))

      case "clustalo" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")))

      case "msaprobs" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")))

      case "muscle" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")))

      case "blammer" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")))

      case "kalign" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")))

      case "mafft" => Seq(("AlignmentViewer", views.html.jobs.resultpanels.msaviewer_tcoffee(jobID)),
        ("Alignment", views.html.jobs.resultpanels.simple(s"/files/$jobID/alignment.clustalw_aln")))

      case "aln2plot" => Seq(("Hydrophobicity", views.html.jobs.resultpanels.image(s"/files/$jobID/hydrophobicity.png")),
        ("SideChainVolume", views.html.jobs.resultpanels.image(s"/files/$jobID/side_chain_volume.png")))

      case "phylip" => Seq(("NeighborJoiningTree", views.html.jobs.resultpanels.tree(s"$jobPath$jobID/results/alignment_nj.tree", "nj_div")),
        ("NeighborJoiningResults", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/alignment.nj")),
        ("UPGMATree", views.html.jobs.resultpanels.tree(s"$jobPath$jobID/results/alignment_upgma.tree", "upgma_div")),
        ("UPGMAResults", views.html.jobs.resultpanels.fileview(s"$jobPath$jobID/results/alignment.upgma")))
    }
  }


  // Contains the tool specifications and generates tool objects accordingly
  lazy val values : Map[String, Tool] = Set(
    // Protblast
    ("protblast", "ProtBlast", "prob", "search", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.STANDARD_DB, paramAccess.MATRIX, paramAccess.EVALUE,
        paramAccess.EVAL_INC_THRESHOLD, paramAccess.GAP_OPEN, paramAccess.GAP_EXT, paramAccess.DESC, paramAccess.PROTBLASTPROGRAM)),

    // HHblits
  ("hhblits", "HHblits", "hhb", "search", "",
    Seq(paramAccess.SEQORALI,paramAccess.HHBLITSDB, paramAccess.EVAL_INC_THRESHOLD, paramAccess.MAXROUNDS,
      paramAccess.PMIN, paramAccess.MAX_LINES, paramAccess.MAX_SEQS, paramAccess.ALIWIDTH, paramAccess.ALIGNMODE)),

    // HHpred
    ("hhpred", "HHpred", "hhp", "search", "",
    Seq(paramAccess.SEQORALI, paramAccess.HHSUITEDB, paramAccess.MSAGENERATION,
        paramAccess.MSA_GEN_MAX_ITER, paramAccess.MIN_COV, paramAccess.MIN_SEQID_QUERY, paramAccess.EVAL_INC_THRESHOLD,
        paramAccess.MAX_LINES, paramAccess.PMIN, paramAccess.ALIWIDTH, paramAccess.ALIGNMODE, paramAccess.SS_SCORING)),

    // HHpred - Manual Template Selection
    ("hhpred_manual", "HHpred - Manual Template Selection", "hhp", "forward", "",  Seq.empty),

    // HHpred - Manual Template Selection
    ("hhpred_automatic", "HHpred - Automatic Template Selection", "hhp", "forward", "",  Seq.empty),

    // PSI-BLAST
    ("psiblast", "PSI-BLAST", "pbl", "search", "", Seq(paramAccess.ALIGNMENT, paramAccess.STANDARD_DB,
      paramAccess.MATRIX,
      paramAccess.NUM_ITER, paramAccess.EVALUE, paramAccess.EVAL_INC_THRESHOLD, paramAccess.GAP_OPEN,
      paramAccess.GAP_EXT, paramAccess.DESC)),

   // T-Coffee
    ("tcoffee", "T-Coffee", "tcf", "alignment", "", Seq(paramAccess.MULTISEQ)),

    // Blammer
    ("blammer", "Blammer", "blam", "alignment", "", Seq(paramAccess.ALIGNMENT,
      paramAccess.MIN_QUERY_COV, paramAccess.MAX_EVAL, paramAccess.MIN_ANCHOR_WITH,
      paramAccess.MAX_SEQID, paramAccess.MAX_SEQS, paramAccess.MIN_COLSCORE)),

    // CLustalOmega
    ("clustalo", "Clustal Omega", "cluo", "alignment", "", Seq(paramAccess.ALIGNMENT)),

    // MSA Probs
    ("msaprobs", "MSAProbs", "msap", "alignment", "", Seq(paramAccess.MULTISEQ)),

    // MUSCLE
    ("muscle", "MUSCLE", "musc", "alignment", "", Seq(paramAccess.MULTISEQ, paramAccess.MAXROUNDS)),

  // MAFFT
    ("mafft", "Mafft", "mft", "alignment", "", Seq(paramAccess.MULTISEQ, paramAccess.GAP_OPEN, paramAccess.OFFSET)),

   // Kalign
      ("kalign", "Kalign", "kal", "alignment", "",
        Seq(paramAccess.MULTISEQ, paramAccess.GAP_OPEN, paramAccess.GAP_EXT, paramAccess.GAP_TERM, paramAccess.BONUSSCORE)),

    // Hmmer
    ("hmmer", "HMMER", "hmmr", "search", "", Seq(paramAccess.ALIGNMENT, paramAccess.STANDARD_DB)),


      // Aln2Plot
    ("aln2plot", "Aln2Plot", "a2pl", "seqanal", "", Seq(paramAccess.ALIGNMENT)),

    // PCOILS
    ("pcoils", "PCOILS", "pco", "seqanal", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.WEIGHTING, paramAccess.MATRIX_PCOILS, paramAccess.RUN_PSIPRED)),

    // FRrped
    ("frpred", "FRpred", "frp", "seqanal", "",Seq(paramAccess.ALIGNMENT)),


    // HHrepID
    ("hhrepid", "HHrepid", "hhr", "seqanal", "",Seq(paramAccess.ALIGNMENT)),


    // MARCOIL
    ("marcoil", "MARCOIL", "mar", "seqanal", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.MATRIX_MARCOIL, paramAccess.TRANSITION_PROBABILITY)),

    // REPPER
    ("repper", "Repper", "rep", "seqanal", "",
      Seq(paramAccess.ALIGNMENT)),

    // TPRpred
    ("tprpred", "TPRpred", "tprp", "seqanal", "",
      Seq(paramAccess.ALIGNMENT)),

    // HHomp
    ("hhomp", "HHomp", "hho", "2ary", "",
      Seq(paramAccess.ALIGNMENT)),

    // Quick 2D
    ("quick2d", "Quick2D", "q2d", "2ary", "",
      Seq(paramAccess.ALIGNMENT)),

    // Ali2D
    ("ali2d", "Ali2D", "a2d", "2ary", "",
      Seq(paramAccess.ALIGNMENT)),

    // Modeller
    ("modeller", "Modeller", "mod", "3ary", "",
      Seq(paramAccess.ALIGNMENT)),

    // ANCESCON
    ("ancescon", "ANCESCON", "anc", "classification", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.LONG_SEQ_NAME)),

    // PHYLIP
    ("phylip", "PHYLIP-NEIGHBOR", "phyn", "classification", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.MATRIX_PHYLIP)),

    // Backtranslate1
    ("backtrans", "Backtranslator", "bac", "utils", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.GENETIC_CODE)),

    // HHfilter
    ("hhfilter", "HHFilter", "hhfi", "utils", "",
      Seq(paramAccess.ALIGNMENT, paramAccess.MAX_SEQID, paramAccess.MIN_SEQID_QUERY, paramAccess.MIN_QUERY_COV,
        paramAccess.NUM_SEQS_EXTRACT))).map { t =>
    t._1  -> tool(t._1, t._2, t._3, t._4, t._5, t._6)
  }.toMap

   // Generates a new Tool object from the Tool specification
    def tool(toolNameShort: String,
             toolNameLong: String,
             toolNameAbbrev: String,
             category: String,
             optional: String,
             params: Seq[Param]) : Tool = {

            lazy val paramGroups = Map(
              "Input" -> Seq(paramAccess.ALIGNMENT.name, paramAccess.STANDARD_DB.name, paramAccess.HHSUITEDB.name,
                paramAccess.PROTBLASTPROGRAM.name, paramAccess.HHBLITSDB.name)
            )
            // Params which are not a part of any group (given by the name)
            lazy val remainParamName : String = "Parameters"
            val remainParams : Seq[String] = params.map(_.name).diff(paramGroups.values.flatten.toSeq)
            val paramMap = params.map(p => p.name -> p).toMap


            val toolitem = Toolitem(
              toolNameShort,
              toolNameLong,
              toolNameAbbrev,
              optional,
              category,
              // Constructs the Parameter specification such that the View can render the input fields
              paramGroups.keysIterator.map { group =>
                group ->  paramGroups(group).filter(params.map(_.name).contains(_)).map(paramMap(_))
              }.toSeq :+
                remainParamName -> remainParams.map(paramMap(_))
            )
            Tool(toolNameShort, toolNameLong, toolNameAbbrev, category,optional,paramMap,
               toolitem, paramGroups)
          }
}
