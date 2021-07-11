package com.salesmanager.core.model.generic;

import java.io.Serializable;
import java.text.Collator;
import java.util.Locale;

import org.hibernate.Hibernate;


/**
 * <p>Entité racine pour la persistence des objets via JPA.</p>
 *
 * @param <E> type de l'entité
 */
public abstract class SalesManagerEntity<K extends Serializable & Comparable<K>, E extends SalesManagerEntity<K, ?>>
		implements Serializable, Comparable<E> {

	private static final long serialVersionUID = -3988499137919577054L;
	
	public static final Collator DEFAULT_STRING_COLLATOR = Collator.getInstance(Locale.FRENCH);
	
	static {
		DEFAULT_STRING_COLLATOR.setStrength(Collator.PRIMARY);
	}
	
	/**
	 * Retourne la valeur de l'identifiant unique.
	 * 
	 * @return id
	 */
	public abstract K getId();

	/**
	 * Définit la valeur de l'identifiant unique.
	 * 
	 * @param id id
	 */
	public abstract void setId(K id);
	
	/**
	 * Indique si l'objet a déjà été persisté ou non
	 * 
	 * @return vrai si l'objet n'a pas encore été persisté
	 */
	public boolean isNew() {
		System.out.println("$#4222#"); System.out.println("$#4221#"); return getId() == null;
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object object) {
		System.out.println("$#4223#"); if (object == null) {
			System.out.println("$#4224#"); return false;
		}
		System.out.println("$#4225#"); if (object == this) {
			System.out.println("$#4226#"); return true;
		}
		
		// l'objet peut être proxyfié donc on utilise Hibernate.getClass() pour sortir la vraie classe
		System.out.println("$#4227#"); if (Hibernate.getClass(object) != Hibernate.getClass(this)) {
			System.out.println("$#4228#"); return false;
		}

		SalesManagerEntity<K, E> entity = (SalesManagerEntity<K, E>) object; // NOSONAR : traité au-dessus mais wrapper Hibernate 
		K id = getId();

		System.out.println("$#4229#"); if (id == null) {
			System.out.println("$#4230#"); return false;
		}

		System.out.println("$#4232#"); System.out.println("$#4231#"); return id.equals(entity.getId());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		
		K id = getId();
		System.out.println("$#4234#"); System.out.println("$#4233#"); hash = 31 * hash + ((id == null) ? 0 : id.hashCode());

		System.out.println("$#4236#"); return hash;
	}

	public int compareTo(E o) {
		System.out.println("$#4237#"); if (this == o) {
			return 0;
		}
		System.out.println("$#4238#"); return this.getId().compareTo(o.getId());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("entity.");
		builder.append(Hibernate.getClass(this).getSimpleName());
		builder.append("<");
		builder.append(getId());
		builder.append("-");
		builder.append(super.toString());
		builder.append(">");
		
		System.out.println("$#4239#"); return builder.toString();
	}
}