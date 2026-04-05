<?php

namespace App\Form;

use App\Entity\ReservationVoyage;
use App\Entity\Voyage;
use Doctrine\ORM\EntityRepository;
use Symfony\Bridge\Doctrine\Form\Type\EntityType;
use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\Extension\Core\Type\ChoiceType;
use Symfony\Component\Form\Extension\Core\Type\DateTimeType;
use Symfony\Component\Form\Extension\Core\Type\IntegerType;
use Symfony\Component\Form\Extension\Core\Type\HiddenType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolver;

class ReservationVoyageType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options): void
    {
        $role = $options['user_role'] ?? 'CLIENT';

        $builder
            ->add('voyage', EntityType::class, [
                'class' => Voyage::class,
                'label' => 'Voyage',
                'placeholder' => 'Sélectionner un voyage',
                'choice_label' => function (Voyage $voyage) {
                    return $voyage->getTitre() . ' — ' . $voyage->getDestination() . ' (' . $voyage->getPrixUnitaire() . ' TND)';
                },
                'query_builder' => function (EntityRepository $er) {
                    return $er->createQueryBuilder('v')
                        ->andWhere('v.statut = :statut')
                        ->setParameter('statut', 'ACTIF')
                        ->orderBy('v.dateDepart', 'ASC');
                },
                'attr' => ['class' => 'form-control'],
            ])
            ->add('dateReservation', DateTimeType::class, [
                'label' => 'Date de réservation',
                'widget' => 'single_text',
                'attr' => ['class' => 'form-control'],
            ])
            ->add('nbrPersonnes', IntegerType::class, [
                'label' => 'Nombre de personnes',
                'attr' => [
                    'min' => 1,
                    'max' => 50,
                    'class' => 'form-control',
                    'placeholder' => '1',
                ],
            ]);

        // Only admin and agent can set status manually
        if (in_array($role, ['ADMIN', 'AGENT'])) {
            $builder->add('statut', ChoiceType::class, [
                'label' => 'Statut',
                'choices' => [
                    'En attente' => 'EN_ATTENTE',
                    'Confirmée' => 'CONFIRMEE',
                    'Annulée' => 'ANNULEE',
                    'Terminée' => 'TERMINEE',
                ],
                'attr' => ['class' => 'form-control'],
            ]);
        }

        // Admin can set any user ID
        if ($role === 'ADMIN') {
            $builder->add('idUtilisateur', IntegerType::class, [
                'label' => 'ID Utilisateur',
                'attr' => [
                    'class' => 'form-control',
                    'placeholder' => 'ID de l\'utilisateur',
                ],
            ]);
        }
    }

    public function configureOptions(OptionsResolver $resolver): void
    {
        $resolver->setDefaults([
            'data_class' => ReservationVoyage::class,
            'user_role' => 'CLIENT',
        ]);
    }
}
