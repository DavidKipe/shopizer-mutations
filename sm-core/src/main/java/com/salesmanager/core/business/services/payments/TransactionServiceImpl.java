package com.salesmanager.core.business.services.payments;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesmanager.core.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.payments.TransactionRepository;
import com.salesmanager.core.business.services.common.generic.SalesManagerEntityServiceImpl;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.payments.Transaction;
import com.salesmanager.core.model.payments.TransactionType;



@Service("transactionService")
public class TransactionServiceImpl  extends SalesManagerEntityServiceImpl<Long, Transaction> implements TransactionService {
	

	private TransactionRepository transactionRepository;
	
	@Inject
	public TransactionServiceImpl(TransactionRepository transactionRepository) {
		super(transactionRepository);
		this.transactionRepository = transactionRepository;
	}
	
	@Override
	public void create(Transaction transaction) throws ServiceException {
		
		//parse JSON string
		String transactionDetails = transaction.toJSONString();
		System.out.println("$#2596#"); if(!StringUtils.isBlank(transactionDetails)) {
			System.out.println("$#2597#"); transaction.setDetails(transactionDetails);
		}
		
		System.out.println("$#2598#"); super.create(transaction);
		
		
	}
	
	@Override
	public List<Transaction> listTransactions(Order order) throws ServiceException {
		
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		for(Transaction transaction : transactions) {
				System.out.println("$#2599#"); if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						System.out.println("$#2600#"); transaction.setTransactionDetails(objects);
					} catch (Exception e) {
						throw new ServiceException(e);
					}
				}
		}
		
		System.out.println("$#2601#"); return transactions;
	}
	
	/**
	 * Authorize
	 * AuthorizeAndCapture
	 * Capture
	 * Refund
	 * 
	 * Check transactions
	 * next transaction flow is
	 * Build map of transactions map
	 * filter get last from date
	 * get last transaction type
	 * verify which step transaction it if
	 * check if target transaction is in transaction map we are in trouble...
	 * 
	 */
	public Transaction lastTransaction(Order order, MerchantStore store) throws ServiceException {
		
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		//ObjectMapper mapper = new ObjectMapper();
		
		//TODO order by date
	    TreeMap<String, Transaction> map = transactions.stream()
	    	      .collect(

	    	    		  Collectors.toMap(
	    	    				  Transaction::getTransactionTypeName, transaction -> transaction,(o1, o2) -> o1, TreeMap::new)
	    	    		  
	    	    		  
	    	    		  );
	    
		  
	    
		//get last transaction
	    Entry<String,Transaction> last = map.lastEntry();
	    
	    String currentStep = last.getKey();
	    
					System.out.println("$#2604#"); System.out.println("Current step " + currentStep);
	    
	    //find next step
	    
					System.out.println("$#2605#"); return last.getValue();
	    


	}

	@Override
	public Transaction getCapturableTransaction(Order order)
			throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		ObjectMapper mapper = new ObjectMapper();
		Transaction capturable = null;
		for(Transaction transaction : transactions) {
			System.out.println("$#2606#"); if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZE.name())) {
				System.out.println("$#2607#"); if(!StringUtils.isBlank(transaction.getDetails())) {
					try {
						@SuppressWarnings("unchecked")
						Map<String,String> objects = mapper.readValue(transaction.getDetails(), Map.class);
						System.out.println("$#2608#"); transaction.setTransactionDetails(objects);
						capturable = transaction;
					} catch (Exception e) {
						throw new ServiceException(e);
					}
				}
			}
			System.out.println("$#2609#"); if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				break;
			}
			System.out.println("$#2610#"); if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				break;
			}
		}
		
		System.out.println("$#2611#"); return capturable;
	}
	
	@Override
	public Transaction getRefundableTransaction(Order order)
		throws ServiceException {
		List<Transaction> transactions = transactionRepository.findByOrder(order.getId());
		Map<String,Transaction> finalTransactions = new HashMap<String,Transaction>();
		Transaction finalTransaction = null;
		for(Transaction transaction : transactions) {
			System.out.println("$#2612#"); if(transaction.getTransactionType().name().equals(TransactionType.AUTHORIZECAPTURE.name())) {
				finalTransactions.put(TransactionType.AUTHORIZECAPTURE.name(),transaction);
				continue;
			}
			System.out.println("$#2613#"); if(transaction.getTransactionType().name().equals(TransactionType.CAPTURE.name())) {
				finalTransactions.put(TransactionType.CAPTURE.name(),transaction);
				continue;
			}
			System.out.println("$#2614#"); if(transaction.getTransactionType().name().equals(TransactionType.REFUND.name())) {
				//check transaction id
				Transaction previousRefund = finalTransactions.get(TransactionType.REFUND.name());
				System.out.println("$#2615#"); if(previousRefund!=null) {
					Date previousDate = previousRefund.getTransactionDate();
					Date currentDate = transaction.getTransactionDate();
					System.out.println("$#2616#"); if(previousDate.before(currentDate)) {
						finalTransactions.put(TransactionType.REFUND.name(),transaction);
						continue;
					}
				} else {
					finalTransactions.put(TransactionType.REFUND.name(),transaction);
					continue;
				}
			}
		}
		
		System.out.println("$#2617#"); if(finalTransactions.containsKey(TransactionType.AUTHORIZECAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.AUTHORIZECAPTURE.name());
		}
		
		System.out.println("$#2618#"); if(finalTransactions.containsKey(TransactionType.CAPTURE.name())) {
			finalTransaction = finalTransactions.get(TransactionType.CAPTURE.name());
		}

		System.out.println("$#2619#"); if(finalTransaction!=null && !StringUtils.isBlank(finalTransaction.getDetails())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String,String> objects = mapper.readValue(finalTransaction.getDetails(), Map.class);
				System.out.println("$#2621#"); finalTransaction.setTransactionDetails(objects);
			} catch (Exception e) {
				throw new ServiceException(e);
			}
		}
		
		System.out.println("$#2622#"); return finalTransaction;
	}

	@Override
	public List<Transaction> listTransactions(Date startDate, Date endDate) throws ServiceException {
		
		System.out.println("$#2623#"); return transactionRepository.findByDates(startDate, endDate);
	}

}
