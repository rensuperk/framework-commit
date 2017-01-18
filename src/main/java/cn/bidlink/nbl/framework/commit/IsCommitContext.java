package cn.bidlink.nbl.framework.commit;

import cn.bidlink.nbl.user.UserContext;
import org.apache.commons.collections.map.HashedMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 暂时用这个防止重复提交
 * Created by ebnew on 2016/8/22.
 */
@Aspect
@Component
public class IsCommitContext {
	
    private final static Map<String,Boolean> isCommit = new HashedMap();
    
    public final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Pointcut("@annotation(CommitController)")
    public void commitPointcut(){

    }
    public static Boolean getIsCommit() {
        return isCommit.get(UserContext.getCurrentUser().getId()) != null ? true :false;
    }

    @Before("commitPointcut()")
    public  void setIsCommit(JoinPoint joinPoint){
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        String key =  declaringTypeName+"@"+UserContext.getCurrentUser().getId();
        if(isCommit.get(key) != null){
            throw new ReCommitException();
        }
        isCommit.put(key,true);
    }
    @AfterThrowing(value = "commitPointcut()",throwing = "e")
    public  void removeIsCommit(JoinPoint joinPoint,Exception e){
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        String key =  declaringTypeName+"@"+UserContext.getCurrentUser().getId();
        if(e != null && e instanceof ReCommitException){
//            throw new ReCommitException();
            return;
        }
        if(isCommit.get(key) != null && isCommit.get(key)){
            isCommit.remove(key);
        }
    }
    @AfterReturning("commitPointcut()")
    public  void removeIsCommit(JoinPoint joinPoint){
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        String key =  declaringTypeName+"@"+UserContext.getCurrentUser().getId();
        if(isCommit.get(key) != null && isCommit.get(key)){
            isCommit.remove(key);
        }
    }

}
