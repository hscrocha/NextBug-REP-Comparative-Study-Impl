/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.persistence.entity;

/**
 *
 * @author Henrique
 */
public class BugCompleteDesc {
    
    private int BugId;
    private String ShortDesc;
    private String FullDesc;
    
/*

select bug_id,bug_when,creation_ts,cf_last_resolved,thetext,short_desc
-- select count(*)
from longdescs join bugs using(bug_id)
where cf_last_resolved > '2009-09-01'
and resolution = 'FIXED'

select bug_id,f.short_desc,comments,cf_last_resolved
from bugs_fulltext f join bugs b using(bug_id)
where cf_last_resolved > '2009-09-01'

 */    
}
