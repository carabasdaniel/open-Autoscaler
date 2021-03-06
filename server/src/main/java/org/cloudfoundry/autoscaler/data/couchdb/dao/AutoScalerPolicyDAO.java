package org.cloudfoundry.autoscaler.data.couchdb.dao;

import java.util.List;

import org.cloudfoundry.autoscaler.data.couchdb.document.AutoScalerPolicy;
import org.cloudfoundry.autoscaler.exceptions.PolicyNotFoundException;

public interface AutoScalerPolicyDAO extends CommonDAO {

	public List<AutoScalerPolicy> findAll();

	public AutoScalerPolicy findByPolicyId(String policyId) throws PolicyNotFoundException;

	public List<AutoScalerPolicy> getAllRecords();

}
